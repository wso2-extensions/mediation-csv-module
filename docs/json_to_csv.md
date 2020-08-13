# JSON to CSV Transformation

[[Overview]](#overview)  [[Operation details]](#operation-details)  [[Sample configuration]](#sample-configuration)

### Overview 

You can use the jsonToCsv operation to transform a JSON payload in to an CSV payload.

### Operation details

**Properties**
* customHeader (CSV Header): 
Set a custom header to the output CSV payload. If this property is not specified, 
Key values of the input would be used as the output CSV headers.
    

### Sample configuration

**Sample request**

Following is a sample CSV request that can be handled by the csvToCsv operation.

```json
[
    {
        "id": "1",
        "name": "De witt Hambidge",
        "email": "dwitt0@newsvine.com",
        "phone_number": "true"
    },
    {
        "id": "2",
        "name": "Brody Dowthwaite",
        "email": "bdowthwaite1@delicious.com",
        "phone_number": "false"
    },
    {
        "id": "3",
        "name": "Catlin Drought",
        "email": "cdrought2@etsy.com",
        "phone_number": "608-510-7991"
    },
    {
        "id": "4",
        "name": "Kissiah Douglass",
        "email": "kdouglass3@squarespace.com",
        "phone_number": "true"
    },
    {
        "id": "5",
        "name": "Robinette Udey",
        "email": "rudey4@nytimes.com",
        "phone_number": "true"
    }
]
```
**Sample Configuration**

Given below is a sample configuration for the csvToCsv operation.
```xml
<CSV.jsonToCsv>
    <customHeader>index,name,email,number</customHeader>
</CSV.jsonToCsv>
```
**Sample response**

Following is a sample response from the operation.

```text
index,name,email,number
1,De witt Hambidge,dwitt0@newsvine.com,true
2,Brody Dowthwaite,bdowthwaite1@delicious.com,false
3,Catlin Drought,cdrought2@etsy.com,608-510-7991
4,Kissiah Douglass,kdouglass3@squarespace.com,true
5,Robinette Udey,rudey4@nytimes.com,true
```
