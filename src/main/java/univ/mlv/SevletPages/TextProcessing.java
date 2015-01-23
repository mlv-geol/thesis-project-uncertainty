/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package univ.mlv.SevletPages;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import univ.mlv.GraphBuilder.GraphConstructor;
import univ.mlv.Structures.Data;
import univ.mlv.development.LoginCheckDB;
import univ.mlv.development.Text;
import univ.mlv.Structures.QueryTriples;

/**
 *
 * @author fadhela-pc
 */
public class TextProcessing extends HttpServlet {

    private static OntModel ontologie;

    public static String getAppDirectory() {
        String appDirectory = TextProcessing.class.getResource("TextProcessing.class").getPath().replaceAll("%20", " ");
        appDirectory = appDirectory.substring(0, appDirectory.indexOf("WEB-INF/"));
        File f = new File(appDirectory);
        appDirectory = f.getAbsolutePath() + File.separator;
        return appDirectory;
    }

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String inputid = request.getParameter("inputID");
        String query = request.getParameter("query");
        String select = request.getParameter("target1");
        String trust2 = request.getParameter("trust2");
        Map<String, Map<String, String>> queries = LoginCheckDB.getQueries(inputid);
        Map<String, String> trusts = LoginCheckDB.getTrusts();
        Map<String, Text> allTexts = LoginCheckDB.getAllTexts();
        Text t = allTexts.get(inputid);
        String user = Data.getUser();
        String trust = trusts.get(user + "_" + t.source);
        String rdf = t.getRdf();
        ontologie = ModelFactory.createOntologyModel();
        InputStream stream = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
        ontologie.read(stream, null, null);
        ontologie.write(System.out);
        if (query == null && select == null) {
            PrintWriter out = response.getWriter();
            try {
                /* TODO output your page here. You may use following sample code. */
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Servlet TextProcessing</title>"
                        //                    + "<script src=\"jquery-1.7.2.js\"></script>"
                        + "<script>function test(){"
                        //                    + "alert(document.getElementById(\"target1\").value);"
                        + "alert(\"ça marcheeeee!!\");"
                        + "}"
                        //                    + "<script>"
                        //                    + "$('#target1').change(function() {"
                        //                    + "alert($(this).attr(\"name\"));"
                        ////                    + "    $.ajax({"
                        ////                    + "        url: 'http://localhost:8085/InterfaceTrust_Uncertainty/TextProcessing',"
                        ////                    + "        data: {selectedValue: $(this).name()},"
                        ////                    + "    });"
                        //                    + "});"
                        + "</script>"
                        + "<link rel=\"stylesheet\" href=\"Style.css\" type=\"text/css\" media=\"all\" />"
                        + "");
                out.println("</head>");
                out.println("<body>"
                        + "<div id=\"navigation\">"
                        + "<ul>"
                        + "<li><a href=\"index.jsp\">HOME</a></li>"
                        + "<li><a href=\"LoginCheck\">User information</a></li>"
                        + "<li><a href=\"ShowText\">Texts</a></li>"
                        + "<li><a href=\"OntoViz\">Ontology</a></li>"
                        + "<li><a href=\"About\">ABOUT</a></li>"
                        + "<li><a href=\"#\">CONTACT</a></li>"
                        + "</ul>"
                        + "<div class=\"cl\">&nbsp;</div>"
                        + "</div>"
                        + "<br>");
                out.println("<div style=\"border: 1px solid gray; \">"
                        + "&nbsp;&nbsp;<h2>The text :</h2><br><div class=\"inptu\"> " + t.content + "</div>");
                out.println("&nbsp;&nbsp;<h2>The source :</h2>&nbsp " + t.source);
                out.println("&nbsp;&nbsp;<h2>The author :</h2>&nbsp" + t.author);
                out.println("&nbsp;&nbsp;<b>The trust granted :</b> <input type=\"text\" name=\"trust\" value=\"" + trust + "\">");
                out.println("<form action=\"OntoViz\"> Do you want to visualize the ontlogy? : <input type=\"submit\" name=\"ontoViz\" value=\"Display\">"
                        + "</form> </div>");
                out.println("<h1>Graph in process...</h1>");
                if (null != rdf && !rdf.isEmpty()) {
                    GraphConstructor g = new GraphConstructor(rdf);
                    if (null != g.getSvg()) {
                        out.println(g.getSvg());
                    } else {
                        out.println("Sorry, the graph can not be created ");
                    }
                    out.println("<br>");
                }


                if (ontologie == null) {
                    out.println("Can not create the model");
                } else {
//                out.println("List of triples : ");
//                StmtIterator listStatements = ontologie.listStatements();
//                while(listStatements.hasNext()){
//                    Statement next = listStatements.next();
//                    out.println(next.asTriple());
//                    
//                }
                    out.println("<h2>Write your query</h2>");
                    out.println("<form method=\"post\" action=\"TextProcessing?inputID=" + inputid + "\">"
                            + "<textarea rows=\"13\" cols=\"60\" name=\"query\"\">"
                            + "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
                            + "\nPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
                            + "\nPREFIX gs: <http://www.geolsemantics.com/onto#>"
                            + "\nPREFIX ical: <http://www.w3.org/2002/12/cal/icaltzd#>"
                            + "\nPREFIX wn: <http://www.w3.org/2006/03/wn/wn20/>"
                            + "\nPREFIX foaf: <http://xmlns.com/foaf/0.1/>"
                            + "\nPREFIX rdfg: <http://www.w3.org/2004/03/trix/rdfg-1>"
                            + "\nPREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                            + "\nPREFIX v: <http://www.w3.org/2006/vcard/ns#>"
                            + "\nPREFIX doac: <http://ramonantonio.net/doac/0.1/>"
                            + "\nPREFIX prov: <http://www.w3.org/ns/prov#>"
                            + "</textarea>"
                            + "<br>"
                            + "<input type=\"checkbox\" name=\"trust2\" value=\"" + trust + "\">Apply confidence degree<br>"
                            + "<input type=\"submit\" value=\"Submit\">" //                        + "</form>"
                            );
                    out.println("<br>"
                            + "<h2> Or, select a query </h2>"
                            + "<br>");
                    out.println(
                            //                        "<Form >"+ 
                            "<Select id=\"target1\" name=\"target1\" action=\"TextProcessing\" onselect=\"test()\">"
                            + "<option value=\"0\">Ask if the author is sur about all what he said?</option>"
                            + "<option value=\"00\">Who is the author? and ho much do you trust it?</option>");
                    for (String key : queries.keySet()) {
                        Map<String, String> get = queries.get(key);
                        out.println("<option value=\"" + get.get("query_id") + "\">" + get.get("query_text") + "</option>");
                    }
                    out.println("</Select>"
                            + "<input type=\"submit\" value=\"Run query\">"
                            + "</Form>");
                }
                out.println("</body>");
                out.println("</html>");
            } finally {
                out.close();
            }
        } else {
            if (query != null) {
                PrintWriter out = response.getWriter();
                try {
                    /* TODO output your page here. You may use following sample code. */
                    out.println("<html>");
                    out.println("<head>");
                    out.println("<title>Servlet TextProcessing</title>");
                    out.println("</head>");
                    out.println("<body>"
                            + "<a href =\"index.jsp\">Home</a> - <a href =\"Text_Views\">Text</a>"
                            + "<br>");
                    out.println("&nbsp;&nbsp;<h2>The text :</h2><br><div class=\"input\"> " + t.content + "</div>");
                    out.println("&nbsp;&nbsp;<h2>The source :</h2> " + t.source);
                    out.println("&nbsp;&nbsp;<h2>The author :</h2> " + t.author);
                    out.println("<b>The trust granted :<b> <input type=\"text\" name=\"trust\" value=\"" + trust + "\">");
                    if (ontologie == null) {
                        out.println("Can not create the model, there is a problem with the RDF");
                    } else {
//                        query = "";
                        if (!query.isEmpty()) {
                            String rewriteQuery = new QueryTriples(query).rewriteQuery();
                            out.println("<h2>The original query is:</h2><br> " + query);
                            out.println("<h2>The rewriting of the query is : </h2><br>" + rewriteQuery);
                            out.println("<h2>Result of the original query:</h2><br>" + executeQuery(query, trust2).replaceAll("<", "").replaceAll(">", ""));
                            out.println("<h2>Result of the rewrited query:</h2><br>" + executeQuery(rewriteQuery, trust2).replaceAll("<", "").replaceAll(">", ""));
                        }
                    }
                    out.println("</body>");
                    out.println("</html>");
                } finally {
                    out.close();
                }
            } else if (select != null) {
                PrintWriter out = response.getWriter();
                try {
                    /* TODO output your page here. You may use following sample code. */
                    out.println("<html>");
                    out.println("<head>");
                    out.println("<title>Servlet TextProcessing</title>");
                    out.println("</head>");
                    out.println("<body>"
                            + "<a href =\"index.jsp\">Home</a> - <a href =\"Text_Views\">Text</a>"
                            + "<br>");
                    if (ontologie == null) {
                        out.println("Can not create the model");
                    } else {
                        String selectedItem = select;
                        query = "";
                        if (null != selectedItem) {
                            if (selectedItem.equals("0")) {
                                //savoir si l'auteur est sûr de ce qu'il sait
                                query = "PREFIX gs: <http://www.geolsemantics.com/onto#>"
                                        + "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
                                        + "PREFIX  v: <http://www.w3.org/2006/vcard/ns#>"
                                        + "Describe ?s Where {"
                                        + "?x rdf:type gs:AuthorUncertainty."
                                        + "?x ?p ?s."
                                        + "}";

                            }
                            if (selectedItem.equals("1")) {
                                //qui est la source, quelle degré de confiance je lui accorde
                            }
                            if (!query.isEmpty()) {
                                String executeQuery = executeQuery(query, trust2).replaceAll("<", "");
                                executeQuery = executeQuery.replaceAll(">", "");
                                out.println("Query: "+query+"<br>Result : \n" +executeQuery );
                            }
                        }
                    }
                    out.println("</body>");
                    out.println("</html>");
                } finally {
                    out.close();
                }
            }
        }

    }

    public String executeQuery(String query, String t) {
        String res = "";
        Query query1 = QueryFactory.create(query);
        QueryExecution qexec = QueryExecutionFactory.create(query1, ontologie);
        Pattern p = Pattern.compile("([0-9]+\\.[0-9])");

        try {
            if (query.contains("ASK")) {
                boolean resultsASK = qexec.execAsk();
                if (resultsASK) {
                    return "true";
                } else {
                    return "false";
                }
            } else if (query.contains("Select")) {
                ResultSet results = qexec.execSelect();
                while (results.hasNext()) {
                    QuerySolution next = results.next();
                    Iterator<String> varNames = next.varNames();
                    while (varNames.hasNext()) {
                        String next1 = varNames.next(); // nomde la variable dans le select
                        System.out.println("next1= " + next1);
                        String e = next.get(next1).toString(); // valeur que prend  la variable
                        if (t != null && !t.isEmpty()) { // appliquer le trust
//                            String[] split_res=next.get(next1).toString().split(" ");
//                            for(String s:split_res){
                            Matcher m = p.matcher(next.get(next1).toString());
                            if (m.find()) {
                                float parseInt = Float.parseFloat(next.get(next1).toString());
                                float parseInt1 = Float.parseFloat(t);
                                e = Float.toString(parseInt * parseInt1);
                            }
                            res = res + "<br> &nbsp;" + "&nbsp; <b>" + next1 + ": </b> &nbsp;" + "&nbsp;" + e;
//                            }
                        } else {
                            res = "&nbsp;" + "&nbsp;" + next1 + "&nbsp;" + "&nbsp;" + e;
                        }

                        res = res + "<br>";
                    }
                }
                ResultSetFormatter.out(System.out, results, query1);
            } else {//Cas du describe
                Model describeModel = qexec.execDescribe();

                res=describeModel.toString();
            }
        } finally {
            qexec.close();
        }

        return res;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}