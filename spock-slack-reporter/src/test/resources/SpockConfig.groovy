import java.text.SimpleDateFormat

// provides unique reports dir version per each day of regression execution
def reportsDir = "build/${new SimpleDateFormat("dd-MMM-yyyy").format(new Date()) as String}"

runner {
    filterStackTrace false
}