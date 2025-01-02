# Project Name: RESTful API for Transaction Management

## Overview
This project implements a RESTful API using Java to manipulate transaction data. It addresses all the use cases outlined in the assessment, as separate endpoints.

## Features
- Developed in Java with a layered architecture.
- RESTful API with endpoints for the given usecases.
- Java Streams for efficient data manipulation.
- Global Exception Handling for consistent error management.
- Comprehensive logging using SLF4J.
- Unit and Integration testing for all functionalities.

## Endpoints
| S.No | Use Case                                      | Method | Endpoint                                               |
|------|-----------------------------------------------|--------|--------------------------------------------------------|
| 1    | All transactions for a given category         | GET    | `/transaction/{category`                               |
| 2    | Total outgoing per category                   | GET    | `/transaction/totalspend/{category}`                   |
| 3    | Highest/Lowest spend for a category in a year | GET    | `/transaction/yearlyStatistics/{category}?year={year}` |
| 4    | Monthly average spend in a category           | GET    | `/transaction/monthlyAverage/{category}`               |
| 5    | Save transactions                             | POST   | `/transaction/save`                                    |

## System Design
- **Controller Layer**: Manages HTTP requests and responses.
- **Service Layer**: Contains business logic for processing transactions.
- **Repository Layer**: Interfaces with an H2 file-based database.
- **DTO Layer**: Separates database models from user interactions.


## Testing
- Unit Tests with JUnit and Mockito.
- Integration Tests to validate end-to-end API functionality.

## Future Enhancements
- Add Swagger/OpenAPI documentation.
- Enhance security with authentication and authorization.
- Implement caching mechanisms for frequently accessed data.


***New learnings during the development ***
1. Unit testing for Private methods - Suggested way to cover is via public classes. If an extensive private method test is needed - use Power Mockito.
2. AssertJ assertions for Map - dedicated assertions like Map.hasSize() instead of Map.size().isEqualTo();

## Sample Data JSON for POST Mapping - transaction/save - Request Body:

[
{
"date": "28/Oct/2020",
"vendor": "PureGym",
"type": "direct_debit",
"amount": 40,
"category": "MyMonthlyDD"
},
{
"date": "30/Sep/2020",
"vendor": "McMillan",
"type": "internet",
"amount": 10,
"category": ""
},
{
"date": "01/Nov/2020",
"vendor": "Morrisons",
"type": "card",
"amount": 10.40,
"category": "Groceries"
},
{
"date": "01/Oct/2020",
"vendor": "M&S",
"type": "card",
"amount": 5.99,
"category": "Groceries"
},
{
"date": "28/Oct/2021",
"vendor": "CYBG",
"type": "direct_debit",
"amount": 600,
"category": "MyMonthlyDD"
}
{
"date": "28/Oct/2020",
"vendor": "CYBG",
"type": "direct_debit",
"amount": 600,
"category": "MyMonthlyDD"
}

]
