<%--
  Created by IntelliJ IDEA.
  User: asimsangram
  Date: 2/23/16
  Time: 3:36 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>

    <style>
        li{
            float: left;
        }
    </style>
</head>

<body>
    <h2>Search Query: "${query}"</h2>
    <ul style="list-style-type: none;">
        <li>
            <table border="1">
                <thead>
                    <th>S.N.</th>
                    <th>Positive Results</th>
                    <th>Score</th>
                </thead>
                <tbody>
                    <g:each in="${positiveResultList}" var="result" status="i">
                        <tr>
                            <td>
                                ${i + 1}
                            </td>
                            <td>
                                <a href="${result.url}">
                                    ${result.heading}
                                </a>
                            </td>
                            <td>
                                ${result.score}
                            </td>
                        </tr>
                    </g:each>
                </tbody>
            </table>
        </li>
        <li>
            <table border="1">
                <thead>
                    <th>S.N.</th>
                    <th>Negative Results</th>
                    <th>Score</th>
                </thead>
                <tbody>
                    <g:each in="${negativeResultList}" var="result" status="i">
                        <tr>
                            <td>
                                ${i + 1}
                            </td>
                            <td>
                                <a href="${result.url}">
                                    ${result.heading}
                                </a>
                            </td>
                            <td>
                                ${result.score}
                            </td>
                        </tr>
                    </g:each>
                </tbody>
            </table>
        </li>
    </ul>
    <div style="display: inline-block;">
        <hr>
        <g:form controller="search" action="results">
            Search Another:<input type="text" name="keywords"/>
            <input type="submit" value="Submit"/>
        </g:form>
    </div>
</body>
</html>