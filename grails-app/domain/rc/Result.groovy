package rc

class Result implements Comparable<Result>{

    @Override
    int compareTo(Result o) {
        if(this.score < o.score)
            return -1
        else
            return 1
    }

    enum Type{

        POSITIVE, NEGATIVE
    }

    String heading
    String url
    Type type
    Query query
    double score

    static constraints = {

        type nullable: false
        heading nullable: false, blank: false
        url nullable: false, blank: false
        query nullable: false
    }

    def getDomain(){

        return url.substring(0, url.lastIndexOf("/"))
    }
}
