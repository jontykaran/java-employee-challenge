## Coding test submission

### Tests are added in the test folder under: `EmployeeServiceTest.java` and `EmployeeControllerTest.java`
### I have created a seprate generic ApiResponse class that takes List and Singular data response. I have not considered checking for status but for improvement, it's better to also check for status.
### For EmployeeService (I had some improvement ideas):
1. I was thinking to implement a map/list to store Employees in memory (sort of cache).
      But then realised the mock API server is the source of truth, and we should depend on it for response.
2. Cache here would have helped to improve data access and manipulation without fetching all employees again and again.
     