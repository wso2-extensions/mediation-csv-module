# CSV Columns Skipper Query

### Overview 

columnsToSkip (Skip Columns) property in CSV to JSON, CSV to XML, CSV to CSV operations supports a simple query
language to config the skipping columns.

### Queries 

#### Single Column

The column selection query can be a single column representing one column in the CSV.
You can represent a column with its index or using the header name for that column.
* Column index : 
Column indexes are starting from 1. You can give a single column index as the column skipper query. 
Eg: `3`
* Column name: 
You can specify a column using its' name. Note that, this feature work only if value of the  `headerPresent` property is
 `Present`. You can give the column name within double quoratations in the columns skipper query,
 Eg: `"email"`
 
 #### Multiple Columns
 You can select multiple columns by combining them with a comma (,).
 Eg: 
 `1,2,3,`
`"name","email"`
`3:"email"`
 
 #### Element Range
 You can specify range of columns in the query. Use colon character (:) to define a range.
 Eg: 
 If you want to skip all the columns from index 1 to 5, then you can use following query,
 `1:5`
 You can use this with column names. If you want to skipp all the column from column `name` to `email`, then use the
  following query,
  `"name":"email"`
 You can mix up indexes and column names in range queries,
 `3:"email"`
 You can use asterisk symbol to represent the last column in case you don't know the number of columns. As an example
  if you want to skipp all the columns from  column `3`, then use following query,
  `3:*`
  
  #### Group Elements
  You can use opening and closing brackets to define a group of elements,
  Eg: `(1:5)`
  `(3,4,"name")`
  `2,3,("name":*)`
  
  #### Not Syntax
  You can use exclamation mark (!) to exclude columns from columns skipper. As an example, if you want to skip all
   the columns from 5 to 10 but want to include 7th column, can use following query,
   `5:10,!7`
  More examples,
  `3:*,!(10:"email")`
  