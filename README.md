# MERP

Project - Refactored Enterprise Resource Planning from a University Project

---

## Overview

Enterprise Resource Planning system developed as a refactored version of a previous university project. The project is organized into independent modules to manage different areas of a business, with a focus on modularity, maintainability, and separation of responsibilities.

This project is primarily focused on business logic, modular architecture, and software design rather than advanced cybersecurity. Some features remain incomplete, particularly the integration between the Accounting and Sales modules. This integration requires linking sales documents with the corresponding accounting journal entries. This was outside the scope of the current refactoring and is currently under revision.

---

## Features

- Modular backend architecture.
- User and profile management.
- Task-based permission management.
- User authentication and authorization with JWT.
- Accounting management.
- Journal entries and accounting reports.
- Sales management.
- Customers and items.
- Invoices and delivery notes.
- React frontend.

---

## Project structure

The project is divided into two main applications:

- **Backend:** REST API responsible for business logic, authentication, and persistence.
- **Frontend:** Web application providing the user interface for interacting with the ERP.

The backend is organized into modules such as:

- **Access:** Users, profiles, permissions, and authentication.
- **Accounting:** Accounts, journal entries, and accounting reports.
- **Sales:** Customers, items, transactions, invoices, and delivery notes.

---

## Technologies used

### Backend

- **Language:** Java
- **Framework:** Spring Boot
- **Database:** PostgreSQL

### Frontend

- **Language:** TypeScript
- **Framework:** React
- **Build tool:** Vite

### Development

- **Version control:** Git
- **Containerization:** Docker

---
