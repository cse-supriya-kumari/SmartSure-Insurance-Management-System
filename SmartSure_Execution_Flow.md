# 🔄 SmartSure: End-to-End API Execution Flow

This guide provides the complete logical sequence for executing all SmartSure APIs. Follow these phases to simulate a real-world insurance lifecycle.

---

## 🔑 Phase 1: Identity & Access Management (Auth Service)
**Goal:** Setup accounts and obtain security tokens.

1.  **Register Admin** (`POST /api/auth/register`)
    - Role: `ADMIN`
2.  **Register Customer** (`POST /api/auth/register`)
    - Role: `USER` (Default)
3.  **Login** (`POST /api/auth/login`)
    - Use credentials for either Admin or Customer.
    - **Note:** Save the `token` from the response. 
4.  **Validate Token** (`GET /api/auth/validate`)
    - Header: `Authorization: Bearer <token>`
5.  **Admin Check: List Users** (`GET /api/auth/users`)
    - Verifies the user registry.
6.  **Admin Check: Total Users** (`GET /api/auth/users/count`)
7.  **Admin Action: Update User Status** (`PUT /api/auth/users/{id}/status`)
    - Use this to activate/deactivate accounts.

---

## 📋 Phase 2: Product & Policy Design (Admin & Policy Services)
**Goal:** Define the insurance products available for purchase.

1.  **Admin: Create Policy Type** (`POST /api/admin/policies`)
    - *Alternative:* `POST /api/policies/types`
2.  **Customer: Browse Policy Types** (`GET /api/policies/types`)
3.  **Admin: Update Policy Details** (`PUT /api/admin/policies/{id}`)
    - *Alternative:* `PUT /api/policies/types/{id}`
4.  **Admin: Delete Obsolete Policy** (`DELETE /api/admin/policies/{id}`)
    - *Alternative:* `DELETE /api/policies/types/{id}`

---

## 🛒 Phase 3: Customer Journey - Policy Purchase (Policy Service)
**Goal:** Customers buying insurance.

1.  **Customer: Purchase Policy** (`POST /api/policies/purchase`)
    - Requires `userId` and `policyTypeId`.
    - Header: `Idempotency-Key: <unique_string>` (to prevent double charges).
2.  **Customer: View My Policies** (`GET /api/policies/user/{userId}`)
3.  **Admin/System: Get Policy Count** (`GET /api/policies/count`)
4.  **Admin/User: View Specific Policy** (`GET /api/policies/{id}`)

---

## 🆘 Phase 4: Claims Lifecycle (Claims & Admin Services)
**Goal:** Filing a claim and getting it approved.

1.  **Customer: Initiate Claim** (`POST /api/claims/initiate`)
    - Status starts as `PENDING`.
2.  **Customer: Upload Evidence** (`POST /api/claims/{claimId}/upload`)
    - Multi-part file upload for documents/bills.
3.  **Customer: Track My Claims** (`GET /api/claims/user/{userId}`)
4.  **Admin: View Pending Claims** (`GET /api/admin/claims/pending`)
    - *Alternative:* `GET /api/claims/pending`
5.  **Admin: Review Claim Details** (`GET /api/admin/claims/{claimId}`)
6.  **Admin: View Claim Documents** (`GET /api/admin/claims/{claimId}/documents`)
7.  **Admin: Update Claim Status (Review)** (`PUT /api/admin/claims/{claimId}/review`)
    - Payload: `{ "status": "APPROVED", "remarks": "..." }`
8.  **System/User: Get Claim Status** (`GET /api/claims/status/{claimId}`)
9.  **System Stats: Total Claims** (`GET /api/claims/count`)
10. **System Stats: Pending Count** (`GET /api/claims/count/pending`)

---

## 📊 Phase 5: Administration & Analytics (Admin Service)
**Goal:** Monitoring system health and business metrics.

1.  **Admin: View System Dashboard** (`GET /api/admin/reports/dashboard`)
    - Real-time KPI visualization (Powered by Redis).
2.  **Admin: Generate Detailed Reports** (`GET /api/admin/reports`)
3.  **Admin: Audit Logs**
    - Check the `admin_audit_logs` database table (created via RabbitMQ events).

---

## 🚀 Pro-Demo Sequence Summary
1.  **Register/Login** (Auth) -> Get Token.
2.  **Create Product** (Admin) -> `Policy ID 1`.
3.  **Purchase Policy** (User) -> `User 2` buys `Policy 1`.
4.  **Raise Claim** (User) -> `Claim 1` for `Policy 1`.
5.  **Audit/Approve** (Admin) -> Approve `Claim 1`.
6.  **Stats** (Admin) -> Check Dashboard for updated counts.
