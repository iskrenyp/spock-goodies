# spock-db-repo

Spock Db Repo is a simple groovy library, which provides an annotation driven local extension for the [Spock Framework], allowing you an easy repository implementation with a powerful validation mechanism, based on [OVal]. 
It facilitates creation of SQL database monitoring specifications within E2E automation solution.

A common use case for utilizing this lib is when high level business logic needs to be validated in the various SQL data stores (repositories). Here's an [example-spock-db-repo] project.

### Example usage

The spock-db-repo lib can be found under mavenCentral repository:

```groovy
    repositories {
        mavenCentral()
    }
```

Add the spock-db-repo lib to your project dependencies, alongside groovy and spock (assuming you're using Gradle as build tool):

```groovy
    testImplementation "com.github.iskrenyp:spock-db-repo:1.0+"
```

Additionally, let's use this mysql db (classicmodels) at https://www.mysqltutorial.org/mysql-sample-database.aspx

```groovy
    testImplementation "mysql:mysql-connector-java:5.1.39"
```
Create a file SqlDataSourceConfig.groovy and place it somewhere withing your project root (src/test/resources is recommended).
Within the config file you can specify multiple SQL database connections in the following format (all fields mandatory)

```groovy
    // The below database was taken and installed from
    // https://www.mysqltutorial.org/mysql-sample-database.aspx
    classicmodels {
        url = 'jdbc:mysql://localhost:3306/classicmodels'
        username = 'root'
        password = '{YOUR_PASSWORD}'
        driver = 'com.mysql.jdbc.Driver'
    }
```

Let's say that the mysql database 'owner' has some requirements for every order created by customers, and we need to test it:
1) The order date must be sooner than the required date
2) The ordered quantity must be >= 6
3) If shipped date is not NULL, then the order must be in one of the following statuses: 'Shipped', 'Disputed', 'Resolved', 'Cancelled'
4) If shipped date is NULL, then the order must be in one of the following statuses: 'Cancelled', 'On Hold', 'In Process'
5) Many column constraints like not null, not empty ot length

Here's a way you can test these requirements, using the spock-db-repo lib in your Spock Specification.
Let's first define the SQL statement, which can give us the needed data. For example:

```mysql-psql
    SELECT
    	orderNumber,
    	orderDate,
    	customerName,
    	orderLineNumber,
    	productName,
    	quantityOrdered,
    	priceEach,
    	requiredDate,
    	shippedDate,
    	status
    FROM
    	orders
    INNER JOIN orderdetails
    		USING (orderNumber)
    INNER JOIN products
    		USING (productCode)
    INNER JOIN customers
    		USING (customerNumber)
    ORDER BY
    	orderNumber,
    	orderLineNumber
    LIMIT 100;
```

Second, let's create a class, which resembles the returned result and add some OVal constraints:

```groovy
    import com.github.iskrenyp.spockdbrepo.api.IValidatableEntity
    import groovy.transform.TupleConstructor
    import net.sf.oval.constraint.Assert
    import net.sf.oval.constraint.NotEmpty
    import net.sf.oval.constraint.NotNull
    
    @TupleConstructor
    class OrderDetailsDto implements IValidatableEntity {
        @NotNull
        Integer orderNumber
        @NotNull
        @Assert(expr = "_value < _this.requiredDate", lang = 'groovy')
        Date orderDate
        @NotNull
        @NotEmpty
        String customerName
        @NotNull
        Integer orderLineNumber
        @NotNull
        @NotEmpty
        String productName
        @NotNull
        @Assert(expr = "_value >= 6", lang = 'groovy')
        Integer quantityOrdered
        @NotNull
        Integer priceEach
        @NotNull
        Date requiredDate
        @Assert(expr = "_value != null ? _this.status in ['Shipped', 'Disputed', 'Resolved', 'Cancelled'] : _this.status in ['Cancelled', 'On Hold', 'In Process']", lang = 'groovy')
        Date shippedDate
        @NotNull
        @NotEmpty
        @Assert(expr = "_value in ['Shipped', 'Disputed', 'Resolved', 'Cancelled', 'On Hold', 'In Process']", lang = 'groovy')
        String status
    }
```

Then in your Specification you can annotate any non-initialized @Shared fields with @Repo and pass the connection name from your config file. 
The annotation will initialize the field with an SqlDataStore instance, and pass the name as constructor param. Calling the select() method, with the sql statement as first param, and closure returning an object implementing the IValidatableEntity interface as second param will map the db response to a collection (List) with the instanced of the type returned in second param.

```groovy
    class ClassicModelsSpec extends Specification {
    
        @Shared @Repo(name='classicmodels') def models
        @Shared ClassicModelsDb classicModelsDb = new ClassicModelsDb('classicmodels')
    
        def "Validate business logic in an app data store"() {
            given: "The business logic SQL select was created to validate the business requirement"
                String selectStatement = """
                                    SELECT 
                                        orderNumber,
                                        orderDate,
                                        customerName,
                                        orderLineNumber,
                                        productName,
                                        quantityOrdered,
                                        priceEach,
                                        requiredDate,
                                        shippedDate,
                                        status 
                                    FROM
                                        orders
                                    INNER JOIN orderdetails 
                                        USING (orderNumber)
                                    INNER JOIN products 
                                        USING (productCode)
                                    INNER JOIN customers 
                                        USING (customerNumber)
                                    ORDER BY 
                                        orderNumber, 
                                        orderLineNumber
                                    LIMIT 100;
                                    """
            when: "The statement is executed"
                List<OrderDetailsDto> orderDetails = models.select(selectStatement) { resp -> new OrderDetailsDto(resp) }
            then: "The business logic for every returned record is validated"
                orderDetails.every { it.validated() }
        }
    }
```
In other words, the above select() method will map every returned row to the type you specify in the closure. 
No need to connect/disconnect from the db server. Everything is done withing the SqlDataStore class. It also
supports inserts (with execute() method), updates (with executeUpdate() method), and calling stored procedures 
with the call() method. All this is done by the wonderful [Groovy Sql Api]:

```groovy
Boolean execute(String sqlStatement) throws DbRepoException 
Integer executeUpdate(String sqlStatement) throws DbRepoException 
def call(String statement, List params=null, Closure closure=null) throws DbRepoException 
```

Alternatively, you can make a small abstraction and make your db monitoring specification even more readable, by
declaring a class which extends SqlDataStore:

```groovy
    @InheritConstructors
    class ClassicModelsDb extends SqlDataStore{
    
        List<IValidatableEntity> run(ClassicModelsSelect validation) {
            select(validation.statement, validation.mapper)
        }
    }
```

Then create an Enum ClassicModelsSelect, which will hold both the sql query and the returned type:

```groovy
enum ClassicModelsSelect {

    ORDER_DETAILS_SELECT("""SELECT 
                                    orderNumber,
                                    orderDate,
                                    customerName,
                                    orderLineNumber,
                                    productName,
                                    quantityOrdered,
                                    priceEach,
                                    requiredDate,
                                    shippedDate,
                                    status 
                                FROM
                                    orders
                                INNER JOIN orderdetails 
                                    USING (orderNumber)
                                INNER JOIN products 
                                    USING (productCode)
                                INNER JOIN customers 
                                    USING (customerNumber)
                                ORDER BY 
                                    orderNumber, 
                                    orderLineNumber
                                LIMIT 100;""", { resp -> new OrderDetailsDto(resp) })

    String statement
    Closure<IValidatableEntity> mapper

    ClassicModelsSelect(String statement, Closure<IValidatableEntity> mapper) {
        this.statement = statement
        this.mapper = mapper
    }
} 
```

And now your test case (same as the first one) becomes:

```groovy
    def "Alternative approach for same test case (doing same thing with little abstraction)" () {
            expect: "The business logic for order details is validated"
                classicModelsDb
                        .run(ORDER_DETAILS_SELECT)
                        .every { it.validated() }
    }
```

[Spock Framework]: <http://spockframework.org/spock/docs/1.3/all_in_one.html>
[OVal]: <http://oval.sourceforge.net/>
[example-spock-db-repo]: <https://github.com/iskrenyp/spock-goodies-examples/tree/master/db-repo-example>
[Groovy Sql Api]: <https://docs.groovy-lang.org/latest/html/api/groovy/sql/Sql.html>