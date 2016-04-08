package rc

class Query {

    String queryString
    Date date = new Date()

    static constraints = {

        queryString nullable: false, blank: false
        date nullable: false
    }
}
