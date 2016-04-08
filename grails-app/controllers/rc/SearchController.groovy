package rc

class SearchController {

    def searchService

    def index() {

    }

    def results(){

        List<Result> positiveResults
        List<Result> negativeResults
        def keywords = params.keywords
        List<Result> results

        results = searchService.getDataFromGoogle(keywords)
        positiveResults = searchService.getPositiveResults(results)
        negativeResults = searchService.getNegativeResults(results)

        [positiveResultList: positiveResults, negativeResultList: negativeResults, query: keywords]
    }
}
