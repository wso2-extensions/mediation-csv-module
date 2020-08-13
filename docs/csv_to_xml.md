# CSV to XML Transformation

[[Overview]](#overview)  [[Operation details]](#operation-details)  [[Sample configuration]](#sample-configuration)

### Overview 

You can use the csvToXml operation to transform a CSV payload in to an XML payload.

### Operation details

**Properties**
* headerPresent (Header): 
Specify whether the CSV input has a header row. Accepting **Absent** or **Present** as values. 
The Default value is **Absent**. 
* valueSeparator (Separator): 
The separator to use in the CSV input. Default is the `,` (comma).
* skipHeader (Skip Headers): Skip the header row or not in the output CSV. 
This accepts **true** or **false** as values. The Default is false. This property is available only if the
 `headerPresent` property is set to `Present`.
* columnsToSkip (Skip Columns): 
Specify columns to skip from the CSV payload. You can specify columns as comma-separated values. 
This supports more complex queries also, you can find full specifications here.
* dataRowsToSkip (Skip Data Rows): 
Number of data rows to skip. The Default is 0. 
    * If `headerPresent` is `Present` then data rows are the rows excluding the first row. 
    * If `headerPresent` is `Absent` then data rows are the rows starting from the first row.
* Root Element Group: 
You can use the properties under this group to config the root XML element of the output XML payload. 
You can find following properties under the root element group.
    * rootElementTag (Tag): Name of the XML tag of the root element.
    * rootElementNamespace (Namespace): Namespace of the root element.
    * rootElementNamespaceURI (Namespace URI): Namespace URI of the root element.
* Group Element Group
The properties under this group is for configuring the group elements of the output XML payload.
You can find following properties under the group element group.
    * groupElementName (Tag): Name of the XML tag of the group element.
    * groupElementNamespace (Namespace): Namespace of the group element.
    * groupElementNamespaceURI (Namespace URI): Namespace URI of the group element.
    

### Sample configuration

**Sample request**

Following is a sample CSV request that can be handled by the csvToCsv operation.

```text
id,name,email,phone_number
1,De witt Hambidge,dwitt0@newsvine.com,true
2,Brody Dowthwaite,bdowthwaite1@delicious.com,false
3,Catlin Drought,cdrought2@etsy.com,608-510-7991
4,Kissiah Douglass,kdouglass3@squarespace.com,true
5,Robinette Udey,rudey4@nytimes.com,true
```
**Sample Configuration**

Given below is a sample configuration for the csvToCsv operation.
```xml
<CSV.csvToCsv>
    <headerPresent>Present</headerPresent>
    <skipHeader>true</skipHeader>
    <dataRowsToSkip>1</dataRowsToSkip>
    <orderByColumn>2</orderByColumn>
    <columnOrdering>Ascending</columnOrdering>
    <columnsToSkip>"phone_number"</columnsToSkip>
    <customHeader>index,name,email</customHeader>
</CSV.csvToCsv>
```
**Sample response**

Following is a sample response from the operation.

```text
index,name,email
2,Brody Dowthwaite,bdowthwaite1@delicious.com
3,Catlin Drought,cdrought2@etsy.com
4,Kissiah Douglass,kdouglass3@squarespace.com
5,Robinette Udey,rudey4@nytimes.com
```
