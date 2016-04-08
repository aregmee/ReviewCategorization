package rc

import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

@Transactional
class SearchService {

    def getDataFromGoogle(String queryString){

        if(!Query.findByQueryStringIlike(queryString)) {
            Query query = new Query(queryString: queryString)
            query.save(flush: true, failOnError: true)
            queryString = queryString.replaceAll(" ", "+");
            List<Result> results = new ArrayList<>()
            String request = "https://www.google.com/search?q=" + queryString + "&num=100";

            try {

                // need http protocol, set this as a Google bot agent :)
                Document doc = Jsoup
                        .connect(request)
                        .userAgent(
                        "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)")
                        .timeout(5000).get();

                // get all links
                Elements elements = doc.select("h3");
                for (Element element : elements) {

                    Elements aTags = element.select("a")
                    for (Element aTag : aTags) {
                        String temp = aTag.attr("href");
                        if (temp.contains("/url?q=") && temp.contains("&sa=")) {
                            Result newResult = new Result(
                                    url: temp.substring(temp.lastIndexOf("/url?q=") + 7, temp.indexOf("&sa=")),
                                    heading: Jsoup.parse(aTag.html()).text(),
                                    query: query
                            )
                            try {
                                newResult = setResultTypeandScore(newResult)
                            } catch (Exception e) {
                                println e.message
                            }
                            if(newResult.type && isValid(newResult)) {
                                newResult.save(failOnError: true, flush: true)

                                results.add(newResult)
                            }
                        }

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return results;
        }
        return Result.findAllByQuery(Query.findByQueryString(queryString))
    }

    def setResultTypeandScore(Result result){

        if(!result.type) {
            HttpURLConnection httpURLConnection;

            StringBuilder stringBuilder = new StringBuilder()
            URL url = new URL("http://gateway-a.watsonplatform.net/calls/url/URLGetTextSentiment?url="
                    + result.url + "&apikey=2896a62d9a3a91bb31dab517fac29d9ffcd3f2e3&outputMode=json")
            httpURLConnection = url.openConnection() as HttpURLConnection

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))

            String line;
            while ((line = bufferedReader.readLine()) != null) {

                stringBuilder.append(line)
            }
            JSONObject jsonObject = new JSONObject(stringBuilder.toString())
            if (jsonObject.get("docSentiment").get("type").equalsIgnoreCase("positive"))
                result.type = Result.Type.POSITIVE
            else if (jsonObject.get("docSentiment").get("type").equalsIgnoreCase("negative"))
                result.type = Result.Type.NEGATIVE

            result.score = Double.parseDouble(jsonObject.get("docSentiment").get("score"))
        }
        return result
    }

    def getPositiveResults(List<Result> results){

        List<Result> positiveResults = new ArrayList<>()
        for(Result result: results){

            if(result.type == Result.Type.POSITIVE)
                positiveResults.add(result)
        }
        Collections.sort(positiveResults)
        Collections.reverse(positiveResults)
        return positiveResults
    }

    def getNegativeResults(List<Result> results){

        List<Result> negativeResults = new ArrayList<>()
        for(Result result: results){

            if(result.type == Result.Type.NEGATIVE)
                negativeResults.add(result)
        }
        Collections.sort(negativeResults)
        Collections.reverse(negativeResults)
        return negativeResults
    }

    def isValid(Result result){

        if(result.score > 0.20 || result.score < -0.20) {
            if (!(result.domain.contains(Constants.WIKIPEDIA) || result.domain.contains(Constants.YOUTUBE) || result.domain.contains(Constants.FACEBOOK)))
                return true
        }else{
            println "score->" + result.score
            println result.heading
        }
        return false
    }
}
