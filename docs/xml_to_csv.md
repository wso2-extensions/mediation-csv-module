# XML to CSV Transformation

[[Overview]](#overview)  [[Operation details]](#operation-details)  [[Sample configuration]](#sample-configuration)

### Overview 

You can use the xmlToCsv operation to transform an XML payload in to an CSV payload.

### Operation details

**Properties**
* customHeader (CSV Header): 
Set a custom header to the output CSV payload. If this property is not specified, Key values of the input would be used as the output CSV headers.
    

### Sample configuration

**Sample request**

Following is a sample XML request that can be handled by the xmlToCsv operation.

```xml
<root>
    <group>
        <id>1</id>
        <name>De witt Hambidge</name>
        <email>dwitt0@newsvine.com</email>
        <phone_number>true</phone_number>
    </group>
    <group>
        <id>2</id>
        <name>Brody Dowthwaite</name>
        <email>bdowthwaite1@delicious.com</email>
        <phone_number>false</phone_number>
    </group>
    <group>
        <id>3</id>
        <name>Catlin Drought</name>
        <email>cdrought2@etsy.com</email>
        <phone_number>608-510-7991</phone_number>
    </group>
    <group>
        <id>4</id>
        <name>Kissiah Douglass</name>
        <email>kdouglass3@squarespace.com</email>
        <phone_number>true</phone_number>
    </group>
    <group>
        <id>5</id>
        <name>Robinette Udey</name>
        <email>rudey4@nytimes.com</email>
        <phone_number>true</phone_number>
    </group>
</root>
```
**Sample Configuration**

Given below is a sample configuration for the xmlToCsv operation.
```xml
<CSV.xmlToCsv>
    <customHeader>index,name,email,number</customHeader>
</CSV.xmlToCsv>
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
