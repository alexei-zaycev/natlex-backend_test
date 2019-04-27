# my solution of test task for backend developer at [Natlex](http://natlex.ru/)

Xls file contains list of geological sections. Section has structure: 

```
{
  name: “Section 1”,
  geologicalClasses: [
    {
      name: “Geo Class 1”,
      code: “GC1” 
    }, 
    ...
  ]
}
```

We need CRUD rest api to get sections.
We need api to import/export xls file that contains sections

Example:
```

Section name | Class 1 name | Class 1 code | Class 2 name | Class 2 code 
Section 1|Geo Class 1|GC1|Geo Class 2|GC2
Section 2|Geo Class 2|GC2
Section 3|Geo Class 5|GCX7
```

Create web based application for processing XLS files.

Requirements
1) small restAPI web-application
2) all data (except files) should be in JSON format
3) should have API for adding a job for file parsing ( “register-job”)
4) “register-job” should return id of the Job
5) File should be parsed in asynchronous way, result should be stored id db 6) should have API for getting result of parsed file by Job ID
7) should have API for searching results by name, code
8) Basic Authorization should be supported (optional)
9) Page for jobs adding and result view (optional)

Technology stack(optional) 
1) Spring
2) Hibernate
3) Spring Data
4) gradle/maven
