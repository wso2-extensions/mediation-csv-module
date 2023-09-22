
# CSV to CSV Transformation  
  
[[Overview]](#overview)  [[Operation details]](#operation-details)  [[Sample configuration]](#sample-configuration)  
  
### Overview   
To transform a CSV payload to another CSV payload in a different format you can use csvToCsv operation.
  
### Operation details  
  
**Properties**  
* headerPresent (Header):   
Specify whether the CSV input has a header row. This accepts **Absent** or **Present** as values. The Default value
 is **Absent**.   
* valueSeparator (Separator):   
Specify the separator to use in the CSV input. Default is the `,` (comma). To use tab as the separator, use the 
  value `tab` to this property. To use space, use the value `space`.  
* skipHeader (Skip Headers): Skip the header row or not in the output CSV.   
This property accepts **true** or **false** as values. The Default is false. This is available only if the value of
 the `headerPresent` property is set to `Present`.  
* dataRowsToSkip (Skip Data Rows):   
Specify number of data rows to skip. The Default value is 0.   
  * If `headerPresent` is `Present`, then data rows are the rows excluding the first row.   
  * If `headerPresent` is `Absent`, then data rows are the rows starting from the first row.  
* orderByColumn (Order by Column):   
Order the CSV content by values of the given column. If you want to specify the column by column index, Provide the
 index of the column (Indexes are starting from 1). To specify the column by column name, give the column name within
  double quotes (e.g., "name"). Specifying the column by column name works only if the value of the `headerPresent
  ` property is `Present`.  
* columnOrdering (Sort Columns):   
This option is enabled if the `orderByColumn` has a value. This option accepts **Ascending** and **Descending** as
 values.  This determines whether the CSV should be ordered ascendingly or descendingly according to the given column.   
The default value is `Ascending`.  
* columnsToSkip (Skip Columns):   
Specify columns to skip from the CSV payload. You can specify the columns as comma-separated values. This property
 supports more complex queries also, you can find full specifications [here](column_skipper_query.md).  
* customHeader (Custom Header): Set a custom header to the output CSV payload. If this property not specified, the
 header for the output CSV is determined as follows,  
  * If value of the `headerPresent` is `Absent` , output CSV would not have a header.  
  * If value of the `headerPresent` is `Present` and `skipHeader` is set as `true`, output CSV would not have a
    header.  
  * If `headerPresent` is `Present` and `skipHeader` is set as `false`, output CSV would have the header of the input
   CSV.    
* customValueSeparator (Separator): Values separator to use in the output CSV. Default is `,` (comma)  
* suppressEscaping (Suppress Escaping): Specify whether to suppress all escaping in the output Csv payload. Default is false. (ie. Escape characters will be present)
  
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
	 <columnsToSkip>"phone_number"</columnsToSkip>	<customHeader>index,name,email</customHeader>
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