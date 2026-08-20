# AutoVault Cloud Requirements

## 1. Document Purpose

This document defines the initial business and system requirements for **AutoVault Cloud**, a cloud-based, multi-tenant automotive workshop and parts inventory management platform.

It describes what the first version must do before implementation begins. It focuses on business behavior, project scope, and testable outcomes rather than Java classes, framework annotations, database mappings, or other implementation details.

All features described here are **planned requirements** unless explicitly marked as implemented.

## 2. Project Overview

AutoVault Cloud will allow multiple independent garages to manage their daily operations online. Each garage is an isolated tenant whose users and business data must remain separate from every other garage. It will help garage staff manage:

- Automotive parts
- Physical inventory
- Stock receiving
- Stock movement history
- Low-stock monitoring
- Customers
- Vehicles
- Workshop job cards
- Operational dashboard information

The intended users within each garage are:

- Garage administrators
- Workshop staff
- Inventory and store staff
- Staff responsible for customer and vehicle records
- Staff responsible for workshop job cards

Development will begin with a working local MVP. The project will not be considered portfolio-ready or CV-ready until the completed application is deployed using AWS.

## 3. Technology Scope

| Area | Technology | Purpose |
|---|---|---|
| Backend | Java 21 and Spring Boot | Business logic and REST API |
| Database | MongoDB | Persistent application data |
| Local development | Docker | Consistent local services and environment |
| Frontend | React | User interface in a later implementation phase |
| Cloud deployment | AWS | Required deployment target for portfolio completion |
| Identity provider | Amazon Cognito | User authentication for the AWS-deployed application |

The first development work will focus on backend behavior and the business domain before the complete frontend is built.

The exact AWS compute, networking, and data-hosting architecture will be decided after the local MVP is stable. Amazon Cognito is the planned identity provider for the AWS-deployed application.

## 4. User Roles

### 4.1 Garage Administrator

A Garage Administrator can perform the following actions only for the garage to which the Administrator belongs:

- Manage users
- Create and update parts
- Correct Part Numbers through a controlled process
- Update Cost Price and Selling Price
- Activate or deactivate parts
- Perform authorized inventory operations
- View stock movement history
- View low-stock information
- Manage customers and vehicles
- Create and update job cards
- View dashboard information

### 4.2 Staff

A Staff user can perform the following actions only for the garage to which the Staff user belongs:

- View and search active parts
- Receive stock when authorized
- View inventory quantities
- View stock movement history when permitted
- View low-stock information
- Manage customer and vehicle information
- Create and update job cards
- View dashboard information relevant to daily operations

### 4.3 Role Scope

- Roles are tenant-scoped. A Garage Administrator is not an administrator of any other garage.
- A platform-wide administrator role is outside the MVP.
- More detailed garage roles and permissions may be introduced later.

## 5. MVP Features

### 5.1 Authentication and Authorization

- Users must authenticate before accessing protected functionality.
- Protected actions must be restricted according to the authenticated user's role.
- Authorization must check both the user's role and garage membership.
- The backend must determine garage membership from trusted authenticated identity information or server-managed membership data.
- The backend must not trust a `garageId` supplied by ordinary request input.

### 5.2 Parts Management

Authorized users must be able to perform the following operations only on Parts belonging to their garage:

- Create parts
- Retrieve parts
- Search parts
- Update permitted part information
- Activate and deactivate parts

Part management must not directly modify physical stock quantity.

### 5.3 Stock Receiving

Authorized users must be able to record physical stock received for an existing active Part belonging to their garage.

A successful stock receipt must:

- Increase the Part's quantity by the accepted amount
- Create one traceable stock movement
- Record the quantity received
- Record the time of the movement
- Identify the affected Part
- Identify the garage that owns the Part and movement
- Identify the responsible user or source where applicable

### 5.4 Stock Movement History

- The system must maintain a history of all physical inventory quantity changes.
- Every successful quantity change must have a corresponding stock movement.
- Historical movements must remain available for auditing.
- Users must only access stock movements belonging to their garage.

### 5.5 Low-Stock Detection

The system must determine whether a Part is normally stocked, low on stock, or out of stock by comparing its current `quantity` with its configured `minimumStockLevel`.

### 5.6 Customers

The system must support creating, retrieving, searching, and updating customer records.

### 5.7 Vehicles

The system must support storing vehicles and associating them with customers where appropriate.

### 5.8 Job Cards

Authorized users must be able to create and manage workshop job cards containing customer, vehicle, and workshop-related information.

### 5.9 Dashboard

The dashboard should provide a simple operational overview including:

- Total active parts
- Low-stock parts
- Out-of-stock parts
- Pending job cards
- Recent workshop activity

All dashboard totals and activity must be calculated only from data belonging to the authenticated user's garage.

## 6. Multi-Tenancy and Garage Data Isolation

### 6.1 Tenant Model

- Each garage is a tenant in AutoVault Cloud.
- A garage may have multiple users.
- For the MVP, each user belongs to exactly one garage.
- A user belonging to multiple garages may be supported in a later phase.
- Tenant isolation is a backend security requirement and must not depend on the frontend hiding data.

### 6.2 Tenant-Owned Data

- Every tenant-owned document must contain a backend-managed `garageId`.
- Tenant-owned documents include Parts, Stock Movements, Customers, Vehicles, Job Cards, and application user membership records.
- `garageId` must be assigned from the authenticated user's trusted garage membership when a document is created.
- Ordinary create and update requests must not be allowed to select or replace `garageId`.
- Every read, search, update, deactivate, inventory, and delete operation must be scoped by `garageId`.
- Relationships between tenant-owned documents must reference documents belonging to the same garage.
- Knowing or guessing another garage's document identifier must not grant access to that document.
- A request for a document outside the authenticated user's garage should normally be treated as not found so that its existence is not disclosed.

### 6.3 Tenant-Scoped Authorization

- Authentication proves the user's identity; authorization determines which garage data and actions that identity may access.
- A valid role never overrides garage isolation.
- A Garage Administrator may manage users and data only within the Administrator's own garage.
- Staff permissions apply only within the Staff user's own garage.
- Platform-wide support or administration is outside the MVP and must not be implied by the Garage Administrator role.

### 6.4 Tenant-Scoped Uniqueness

- A normalized Part Number must be unique within one garage.
- Different garages may use the same normalized Part Number.
- Database uniqueness protection must use the combination of `garageId` and normalized `partNumber`.

### 6.5 Identity and Token Security

- Amazon Cognito is the planned identity provider for the AWS deployment.
- The Spring Boot API must validate the signature, issuer, and expiry of accepted access tokens before trusting identity information.
- Garage membership used for authorization must come from trusted, server-controlled membership data or a protected identity claim; it must not come from editable client input.
- Tenant-identifying identity attributes must not be editable by ordinary users.
- Passwords must not be stored by the AutoVault Cloud application when authentication is delegated to Amazon Cognito.
- Access tokens, passwords, database credentials, and other secrets must not be written to application logs.

### 6.6 Tenant Isolation Testing

Automated tests must prove that:

- A user can read and modify authorized data belonging to the user's garage.
- A user cannot read, search, update, deactivate, delete, or change inventory for another garage's data.
- Supplying another garage's `garageId` in request input cannot change the authenticated tenant scope.
- Guessing another garage's valid document `id` does not expose or modify that document.
- The same normalized Part Number may exist in two different garages.
- Duplicate normalized Part Numbers are rejected within the same garage.

## 7. Out of Scope for the Initial MVP

The following features are intentionally outside the first development milestone:

- Estimates
- Invoices
- Payments
- GRN workflows
- Product returns
- Refunds and credit handling
- Advanced financial and operational reports
- Email and SMS notifications
- Multiple branches within a single garage tenant
- A single user belonging to multiple garages
- Platform-wide administration across garages
- Mobile applications
- Advanced printing
- Complex accounting functionality

These features may be introduced after the MVP is stable.

## 8. Part Requirements

| Field | Meaning | Required? | Business Rule |
|---|---|---:|---|
| `id` | System-generated unique identifier | Yes | Generated by the system |
| `garageId` | Owning garage identifier | Yes | Assigned by the backend from authenticated garage membership; immutable through normal Part operations |
| `partNumber` | Stable business identifier | Yes | Unique after normalization within the owning garage |
| `name` | Main descriptive name | Yes | Must not be blank |
| `description` | Additional description | No | Optional free text |
| `category` | Logical group | No | Used for organization and search |
| `brand` | Manufacturer or brand | No | Optional |
| `condition` | Physical condition | Yes | `NEW` or `USED` for the MVP |
| `quantity` | Current physical inventory quantity | Yes | System-managed whole number; starts at `0` |
| `minimumStockLevel` | Low-stock threshold | Yes | Non-negative whole number |
| `costPrice` | Cost of acquiring one unit | Yes | Must be zero or greater; interpreted in LKR |
| `sellingPrice` | Standard selling price | Yes | Must be zero or greater; interpreted in LKR |
| `storageLocation` | Physical storage location | No | Optional |
| `active` | Availability for normal use | Yes | Defaults to active |
| `createdAt` | Creation timestamp | Yes | Generated automatically and stored in UTC |
| `updatedAt` | Last-modified timestamp | Yes | Updated automatically and stored in UTC |

## 9. Quantity Ownership

The `quantity` field represents the current physical amount of stock.

- Users must not directly set or edit `quantity` through normal Part creation or Part update operations.
- A newly created Part always starts with `quantity = 0`.
- `quantity` must always be a non-negative whole number.
- `minimumStockLevel` must always be a non-negative whole number.
- Fractional quantities such as `1.5` are not permitted for the MVP.

Physical quantity may change only through controlled inventory operations, including:

- Stock receiving
- Authorized stock adjustments
- Stock issuing in a later phase
- Product returns in a later phase

Every successful physical quantity change must create a corresponding stock movement. This keeps Part management separate from inventory operations.

## 10. Part Number Rules

### 10.1 Whitespace Normalization

Leading and trailing whitespace must be removed before validation, storage, and comparison.

For example, `" BP-001 "` is normalized to `"BP-001"`.

### 10.2 Case-Insensitive Uniqueness

Part Number uniqueness is case-insensitive within a garage. Therefore, `BP-001`, `bp-001`, and `Bp-001` must be treated as the same Part Number during duplicate detection and lookup for one garage. Another garage may independently use the same normalized Part Number.

### 10.3 Permitted Characters

For the MVP, a Part Number may contain:

- Letters
- Numbers
- Hyphens
- Underscores

Internal spaces are not permitted.

Valid examples:

- `BP-001`
- `FILTER_25`
- `A123`
- `VW-7P0698151`

Invalid examples:

- `BP 001`
- `PART@001`
- `ABC/123`

The character policy may be expanded later if real workshop data requires additional characters.

### 10.4 Part Number Changes

- A Part Number should normally remain unchanged after creation.
- Staff users must not change a Part Number.
- A Garage Administrator may perform a controlled correction for a genuine data-entry mistake within the Administrator's garage.
- A corrected Part Number must still satisfy all normalization, character, and uniqueness rules.

## 11. Part Business Rules

### 11.1 Identity and Description

- `garageId` must be assigned by the backend from authenticated garage membership and must not be accepted from ordinary Part request input.
- `partNumber` must be provided, normalized, valid, and unique without regard to letter case within the owning garage.
- `name` must not be blank.
- `NEW` and `USED` are the only supported Part conditions for the MVP.
- Duplicate normalized Part Numbers must be rejected within the same garage.

### 11.2 Inventory

- A new Part always starts with `quantity = 0`.
- Normal Part creation and update operations cannot directly change `quantity`.
- `quantity` and `minimumStockLevel` must be non-negative whole numbers.
- Every physical quantity change must occur through a controlled inventory operation.
- Every successful physical quantity change must create a stock movement.

### 11.3 Stock Status

| Status | Rule |
|---|---|
| Out of Stock | `quantity == 0` |
| Low Stock | `quantity > 0 && quantity <= minimumStockLevel` |
| Normally Stocked | `quantity > minimumStockLevel` |

### 11.4 Prices

- `costPrice` and `sellingPrice` must not be negative.
- Prices are interpreted in Sri Lankan Rupees (LKR) for the MVP.
- `sellingPrice` may be lower than `costPrice` for legitimate business reasons.
- Only a Garage Administrator from the owning garage may update Cost Price or Selling Price during the MVP.
- A price change does not alter physical quantity and does not create a stock movement.

### 11.5 Active Status

- New Parts are active by default.
- Inactive Parts are excluded from normal active-Part searches.
- An inactive Part may retain physical stock.
- A Part containing stock may be deactivated.
- Deactivation must not change or remove stock.
- Deactivation is a business-status change, not an inventory movement.
- Parts referenced by historical records should be deactivated rather than permanently deleted.

### 11.6 Timestamps

- Persisted timestamps must be stored consistently in UTC.
- Interfaces may convert UTC timestamps to the user's local timezone for display.

## 12. First Workflow: Create and Retrieve a Part

### 12.1 Create a Part

1. An authorized user submits new Part information.
2. The application obtains `garageId` from the authenticated user's trusted garage membership.
3. The application ignores or rejects any attempt to select `garageId` through ordinary Part request input.
4. The application removes leading and trailing whitespace from `partNumber`.
5. The application validates all required fields.
6. The application validates the permitted Part Number format.
7. The application checks the same garage for an existing normalized Part Number without regard to letter case.
8. The application rejects duplicate or invalid information.
9. The application validates `minimumStockLevel`, `costPrice`, and `sellingPrice`.
10. The system initializes `quantity` to `0` without accepting an initial quantity from the user.
11. The system initializes the Part as active unless another permitted state is deliberately selected.
12. The system records `createdAt` and `updatedAt` in UTC.
13. The valid Part is stored in MongoDB with a system-generated identifier and the authenticated `garageId`.
14. The newly saved Part is returned to the caller.

### 12.2 Retrieve a Part

A saved Part must be retrievable using:

- Its system-generated `id`
- Its `partNumber`

Part Number lookup must use the same normalization and case-insensitive matching rules as creation.

Every retrieval must also match the authenticated user's `garageId`. A matching identifier or Part Number in another garage must not be returned.

## 13. Stock Receiving Workflow

1. An authorized user selects an existing active Part belonging to the user's garage.
2. The user enters the quantity being physically received.
3. The application verifies that the receipt quantity is a positive whole number.
4. Zero, negative, and fractional receipt quantities are rejected.
5. The application verifies that the Part belongs to the authenticated user's garage and is valid for the operation.
6. The Part's quantity increases by exactly the accepted receipt quantity.
7. Exactly one corresponding stock movement is created.
8. The movement records the owning `garageId`, Part, quantity, movement type, timestamp, and responsible user or source where applicable.
9. The updated quantity is returned or made available to the caller.

The quantity update and its stock movement form one successful inventory operation. The system must not commit one without the other.

## 14. Concurrent Stock Receiving

The system must preserve the correct quantity when multiple valid stock operations occur at nearly the same time.

Example scenario:

1. Initial quantity is `0`.
2. One operation receives `5`.
3. Another operation receives `3`.
4. Both operations complete successfully.

Required result:

- Final quantity is `8`.
- One movement records receipt of `5`.
- One movement records receipt of `3`.
- Neither operation overwrites or loses the other operation's quantity change.

The technical implementation of atomic updates, concurrency control, and MongoDB transactions will be studied before stock receiving is implemented.

## 15. Initial Acceptance Criteria

### 15.1 Part Creation

- A valid Part can be created successfully.
- A new Part receives a unique system-generated `id`.
- A new Part receives `garageId` from the authenticated user rather than ordinary request input.
- A new Part always starts with `quantity = 0`.
- A caller cannot provide a different initial quantity through normal Part creation.
- Blank `partNumber` and `name` values are rejected.
- Leading and trailing whitespace is removed from `partNumber`.
- `BP-001` and `bp-001` are treated as duplicates within the same garage.
- Two different garages may each create their own normalized `BP-001` Part.
- Unsupported Part Number characters are rejected.
- Negative or fractional `minimumStockLevel` values are rejected.
- Negative prices are rejected.
- A Selling Price lower than Cost Price is allowed.
- Valid `NEW` and `USED` Parts can be created.
- Unsupported Part conditions are rejected.
- Invalid input does not create a partial Part record.

### 15.2 Part Retrieval

- A saved Part can be retrieved by `id`.
- A saved Part can be retrieved by `partNumber`.
- Part Number lookup applies the documented normalization rules.
- Retrieval by `id` or `partNumber` returns a Part only when its `garageId` matches the authenticated user's garage.
- A valid identifier belonging to another garage does not expose that Part.

### 15.3 Quantity Management

- `quantity` and `minimumStockLevel` are represented as whole numbers.
- Normal Part updates cannot directly modify `quantity`.
- Receiving stock increases quantity by exactly the received amount.
- One successful stock receipt creates exactly one corresponding movement.
- Zero, negative, and fractional receipt quantities are rejected.
- Price and active-status changes do not alter quantity.
- A stock operation cannot change the quantity of a Part belonging to another garage.

### 15.4 Concurrent Receiving

Given an initial quantity of `0`, if one successful operation receives `5` while another receives `3`:

- Final quantity must be `8`.
- Exactly two corresponding movements must exist.
- One movement must represent `5` and the other `3`.
- Neither operation may overwrite or lose the result of the other.

### 15.5 Stock Status

- A Part with `quantity = 0` is Out of Stock.
- A Part with `quantity > 0 && quantity <= minimumStockLevel` is Low Stock.
- A Part with `quantity > minimumStockLevel` is not Low Stock.

### 15.6 Active Status

- New Parts are active by default.
- Inactive Parts are excluded from normal active-Part searches.
- An inactive Part may retain stock.
- Deactivating a Part must not remove its inventory.
- Historical references to an inactive Part remain valid.

### 15.7 Prices

- Prices use LKR for the MVP.
- Only a Garage Administrator from the owning garage may update Cost Price and Selling Price.
- Selling Price may be lower than Cost Price.
- Price changes create no stock movement.

### 15.8 Timestamps

- `createdAt` and `updatedAt` are recorded automatically.
- Persisted timestamps use UTC consistently.

### 15.9 Tenant Isolation and Authorization

- Unauthenticated requests cannot access protected garage data.
- A user can access authorized data belonging to the user's garage.
- A user cannot read, search, update, deactivate, delete, or change inventory for another garage's data.
- Garage ownership is derived from trusted authenticated identity or server-managed membership data.
- Request input cannot override the authenticated `garageId`.
- Role checks and garage ownership checks are both enforced.
- Cross-garage access attempts do not reveal whether the target document exists.
- Repository and service tests include explicit cross-garage denial scenarios.

## 16. Development Milestones

### 16.1 Milestone 1: Local MVP

The local MVP is complete when the following functionality works correctly in a local environment:

- Spring Boot backend
- MongoDB persistence
- Authentication and authorization
- Multi-tenant garage data isolation
- Parts management
- Stock receiving
- Stock movement history
- Low-stock detection
- Customers
- Vehicles
- Job cards
- Basic dashboard
- Automated tests for important business rules
- Automated tests for cross-garage access denial

### 16.2 Milestone 2: Portfolio Completion

AutoVault Cloud is portfolio-ready when:

- The working application is deployed using AWS.
- User authentication is integrated with Amazon Cognito.
- The application operates outside the developer's local machine.
- The deployment demonstrates practical cloud deployment experience.
- The deployed backend enforces role-based authorization and garage data isolation.

The exact AWS architecture will be selected later.

## 17. Future Phases

After the MVP is stable, AutoVault Cloud may be expanded with:

- Estimates
- Invoices
- Payments
- GRN documentation
- Product returns
- Refund and credit handling
- Advanced inventory and financial reports
- Email and SMS notifications
- Multiple branches within a garage tenant
- Users who belong to multiple garages
- Platform-wide support administration with separately designed controls
- Advanced dashboard analytics
- Printable workshop documents
- Mobile access
- More detailed user roles
- Audit logging
- Advanced cloud infrastructure

## 18. Scope Principle

The first implementation should remain intentionally focused on:

- Parts
- Controlled inventory quantity
- Stock movements
- Customers
- Vehicles
- Job cards
- Authentication
- Garage tenant isolation
- Basic operational visibility

Customers, Vehicles, Job Cards, Authentication, Garage Membership, and Dashboard are intentionally defined only at a high level in this document. Each module will receive detailed business rules and acceptance criteria before implementation begins.

Advanced workshop workflows should be introduced only after the MVP domain model and its core business rules are stable.
