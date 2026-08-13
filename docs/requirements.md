# AutoVault Cloud Requirements

## 1. Document Purpose

This document defines the initial business and system requirements for **AutoVault Cloud**, a cloud-based automotive workshop and parts inventory management platform.

It describes what the first version must do before implementation begins. It focuses on business behavior, project scope, and testable outcomes rather than Java classes, framework annotations, database mappings, or other implementation details.

All features described here are **planned requirements** unless explicitly marked as implemented.

## 2. Project Overview

AutoVault Cloud will centralize the daily operations of an automotive workshop. It will help workshop staff manage:

- Automotive parts
- Physical inventory
- Stock receiving
- Stock movement history
- Low-stock monitoring
- Customers
- Vehicles
- Workshop job cards
- Operational dashboard information

The intended users are:

- Workshop administrators
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

The first development work will focus on backend behavior and the business domain before the complete frontend is built.

The exact AWS services and deployment architecture will be decided after the local MVP is stable.

## 4. User Roles

### 4.1 Administrator

An Administrator can:

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

A Staff user can:

- View and search active parts
- Receive stock when authorized
- View inventory quantities
- View stock movement history when permitted
- View low-stock information
- Manage customer and vehicle information
- Create and update job cards
- View dashboard information relevant to daily operations

More detailed roles and permissions may be introduced later.

## 5. MVP Features

### 5.1 Authentication and Authorization

- Users must authenticate before accessing protected functionality.
- Protected actions must be restricted according to the authenticated user's role.

### 5.2 Parts Management

Authorized users must be able to:

- Create parts
- Retrieve parts
- Search parts
- Update permitted part information
- Activate and deactivate parts

Part management must not directly modify physical stock quantity.

### 5.3 Stock Receiving

Authorized users must be able to record physical stock received for an existing active Part.

A successful stock receipt must:

- Increase the Part's quantity by the accepted amount
- Create one traceable stock movement
- Record the quantity received
- Record the time of the movement
- Identify the affected Part
- Identify the responsible user or source where applicable

### 5.4 Stock Movement History

- The system must maintain a history of all physical inventory quantity changes.
- Every successful quantity change must have a corresponding stock movement.
- Historical movements must remain available for auditing.

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

## 6. Out of Scope for the Initial MVP

The following features are intentionally outside the first development milestone:

- Estimates
- Invoices
- Payments
- GRN workflows
- Product returns
- Refunds and credit handling
- Advanced financial and operational reports
- Email and SMS notifications
- Multiple workshop branches
- Mobile applications
- Advanced printing
- Complex accounting functionality

These features may be introduced after the MVP is stable.

## 7. Part Requirements

| Field | Meaning | Required? | Business Rule |
|---|---|---:|---|
| `id` | System-generated unique identifier | Yes | Generated by the system |
| `partNumber` | Stable business identifier | Yes | Unique after normalization |
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

## 8. Quantity Ownership

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

## 9. Part Number Rules

### 9.1 Whitespace Normalization

Leading and trailing whitespace must be removed before validation, storage, and comparison.

For example, `" BP-001 "` is normalized to `"BP-001"`.

### 9.2 Case-Insensitive Uniqueness

Part Number uniqueness is case-insensitive. Therefore, `BP-001`, `bp-001`, and `Bp-001` must be treated as the same Part Number during duplicate detection and lookup.

### 9.3 Permitted Characters

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

### 9.4 Part Number Changes

- A Part Number should normally remain unchanged after creation.
- Staff users must not change a Part Number.
- An Administrator may perform a controlled correction for a genuine data-entry mistake.
- A corrected Part Number must still satisfy all normalization, character, and uniqueness rules.

## 10. Part Business Rules

### 10.1 Identity and Description

- `partNumber` must be provided, normalized, valid, and unique without regard to letter case.
- `name` must not be blank.
- `NEW` and `USED` are the only supported Part conditions for the MVP.
- Duplicate normalized Part Numbers must be rejected.

### 10.2 Inventory

- A new Part always starts with `quantity = 0`.
- Normal Part creation and update operations cannot directly change `quantity`.
- `quantity` and `minimumStockLevel` must be non-negative whole numbers.
- Every physical quantity change must occur through a controlled inventory operation.
- Every successful physical quantity change must create a stock movement.

### 10.3 Stock Status

| Status | Rule |
|---|---|
| Out of Stock | `quantity == 0` |
| Low Stock | `quantity > 0 && quantity <= minimumStockLevel` |
| Normally Stocked | `quantity > minimumStockLevel` |

### 10.4 Prices

- `costPrice` and `sellingPrice` must not be negative.
- Prices are interpreted in Sri Lankan Rupees (LKR) for the MVP.
- `sellingPrice` may be lower than `costPrice` for legitimate business reasons.
- Only an Administrator may update Cost Price or Selling Price during the MVP.
- A price change does not alter physical quantity and does not create a stock movement.

### 10.5 Active Status

- New Parts are active by default.
- Inactive Parts are excluded from normal active-Part searches.
- An inactive Part may retain physical stock.
- A Part containing stock may be deactivated.
- Deactivation must not change or remove stock.
- Deactivation is a business-status change, not an inventory movement.
- Parts referenced by historical records should be deactivated rather than permanently deleted.

### 10.6 Timestamps

- Persisted timestamps must be stored consistently in UTC.
- Interfaces may convert UTC timestamps to the user's local timezone for display.

## 11. First Workflow: Create and Retrieve a Part

### 11.1 Create a Part

1. An authorized user submits new Part information.
2. The application removes leading and trailing whitespace from `partNumber`.
3. The application validates all required fields.
4. The application validates the permitted Part Number format.
5. The application checks for an existing normalized Part Number without regard to letter case.
6. The application rejects duplicate or invalid information.
7. The application validates `minimumStockLevel`, `costPrice`, and `sellingPrice`.
8. The system initializes `quantity` to `0` without accepting an initial quantity from the user.
9. The system initializes the Part as active unless another permitted state is deliberately selected.
10. The system records `createdAt` and `updatedAt` in UTC.
11. The valid Part is stored in MongoDB with a system-generated identifier.
12. The newly saved Part is returned to the caller.

### 11.2 Retrieve a Part

A saved Part must be retrievable using:

- Its system-generated `id`
- Its `partNumber`

Part Number lookup must use the same normalization and case-insensitive matching rules as creation.

## 12. Stock Receiving Workflow

1. An authorized user selects an existing active Part.
2. The user enters the quantity being physically received.
3. The application verifies that the receipt quantity is a positive whole number.
4. Zero, negative, and fractional receipt quantities are rejected.
5. The application verifies that the Part is valid for the operation.
6. The Part's quantity increases by exactly the accepted receipt quantity.
7. Exactly one corresponding stock movement is created.
8. The movement records the Part, quantity, movement type, timestamp, and responsible user or source where applicable.
9. The updated quantity is returned or made available to the caller.

The quantity update and its stock movement form one successful inventory operation. The system must not commit one without the other.

## 13. Concurrent Stock Receiving

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

## 14. Initial Acceptance Criteria

### 14.1 Part Creation

- A valid Part can be created successfully.
- A new Part receives a unique system-generated `id`.
- A new Part always starts with `quantity = 0`.
- A caller cannot provide a different initial quantity through normal Part creation.
- Blank `partNumber` and `name` values are rejected.
- Leading and trailing whitespace is removed from `partNumber`.
- `BP-001` and `bp-001` are treated as duplicates.
- Unsupported Part Number characters are rejected.
- Negative or fractional `minimumStockLevel` values are rejected.
- Negative prices are rejected.
- A Selling Price lower than Cost Price is allowed.
- Valid `NEW` and `USED` Parts can be created.
- Unsupported Part conditions are rejected.
- Invalid input does not create a partial Part record.

### 14.2 Part Retrieval

- A saved Part can be retrieved by `id`.
- A saved Part can be retrieved by `partNumber`.
- Part Number lookup applies the documented normalization rules.

### 14.3 Quantity Management

- `quantity` and `minimumStockLevel` are represented as whole numbers.
- Normal Part updates cannot directly modify `quantity`.
- Receiving stock increases quantity by exactly the received amount.
- One successful stock receipt creates exactly one corresponding movement.
- Zero, negative, and fractional receipt quantities are rejected.
- Price and active-status changes do not alter quantity.

### 14.4 Concurrent Receiving

Given an initial quantity of `0`, if one successful operation receives `5` while another receives `3`:

- Final quantity must be `8`.
- Exactly two corresponding movements must exist.
- One movement must represent `5` and the other `3`.
- Neither operation may overwrite or lose the result of the other.

### 14.5 Stock Status

- A Part with `quantity = 0` is Out of Stock.
- A Part with `quantity > 0 && quantity <= minimumStockLevel` is Low Stock.
- A Part with `quantity > minimumStockLevel` is not Low Stock.

### 14.6 Active Status

- New Parts are active by default.
- Inactive Parts are excluded from normal active-Part searches.
- An inactive Part may retain stock.
- Deactivating a Part must not remove its inventory.
- Historical references to an inactive Part remain valid.

### 14.7 Prices

- Prices use LKR for the MVP.
- Only an Administrator may update Cost Price and Selling Price.
- Selling Price may be lower than Cost Price.
- Price changes create no stock movement.

### 14.8 Timestamps

- `createdAt` and `updatedAt` are recorded automatically.
- Persisted timestamps use UTC consistently.

## 15. Development Milestones

### 15.1 Milestone 1: Local MVP

The local MVP is complete when the following functionality works correctly in a local environment:

- Spring Boot backend
- MongoDB persistence
- Authentication and authorization
- Parts management
- Stock receiving
- Stock movement history
- Low-stock detection
- Customers
- Vehicles
- Job cards
- Basic dashboard
- Automated tests for important business rules

### 15.2 Milestone 2: Portfolio Completion

AutoVault Cloud is portfolio-ready when:

- The working application is deployed using AWS.
- The application operates outside the developer's local machine.
- The deployment demonstrates practical cloud deployment experience.

The exact AWS architecture will be selected later.

## 16. Future Phases

After the MVP is stable, AutoVault Cloud may be expanded with:

- Estimates
- Invoices
- Payments
- GRN documentation
- Product returns
- Refund and credit handling
- Advanced inventory and financial reports
- Email and SMS notifications
- Multiple workshop branches
- Advanced dashboard analytics
- Printable workshop documents
- Mobile access
- More detailed user roles
- Audit logging
- Advanced cloud infrastructure

## 17. Scope Principle

The first implementation should remain intentionally focused on:

- Parts
- Controlled inventory quantity
- Stock movements
- Customers
- Vehicles
- Job cards
- Authentication
- Basic operational visibility

Customers, Vehicles, Job Cards, Authentication, and Dashboard are intentionally defined only at a high level in this document. Each module will receive detailed business rules and acceptance criteria before implementation begins.

Advanced workshop workflows should be introduced only after the MVP domain model and its core business rules are stable.
