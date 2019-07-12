package simbio.main;
/**
 * Similarity Servlet Controller 
 * @author Samira Babalou (samira[dot]babalou[at]uni_jean[dot]de)
 */

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;

import slib.sml.sm.core.metrics.ic.dspo.parameters;
import slib.utils.impl.Timer;

@WebServlet("/SimServlet")
public class SimServlet extends HttpServlet {
	private static final long serialVersionUID = 102831973239L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SimServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Timer t = new Timer();
		t.start();
		try {
			String Term1 = "", Term2 = "", Simflag = "", OntFlag = "", Term1Source = "", Term2Source = "", Hiter = "",
					beta = "", landa = "", fitThr = "", defSet = "", res = "", Term1OntPath = null, Term2OntPath = null,
					gRecaptchaResponse = "", oldFile1 = null, oldFile2 = null;
			String UPLOAD_DIRECTORY = getServletContext().getRealPath("/") + "uploads\\";
			String[] Terms = null;
			boolean recaptchaCheck = false;

			List<FileItem> multiFiles = new ServletFileUpload(new DiskFileItemFactory())
					.parseRequest(new ServletRequestContext(request));
			;
			String submitType = "";

			if (!new File(UPLOAD_DIRECTORY).exists())
				new File(UPLOAD_DIRECTORY).mkdir();

			for (FileItem item : multiFiles) {
				if (item.getFieldName().equals("Term1OntPath")) {
					if (item.getName() != "") {
						String name = new File(item.getName()).getName();
						if (name != "") {
							Term1OntPath = UPLOAD_DIRECTORY + name;
							item.write(new File(Term1OntPath));
						}
					}

				} else if (item.getFieldName().equals("Term2OntPath")) {
					if (item.getName() != "") {
						String name = new File(item.getName()).getName();
						if (name != "") {
							Term2OntPath = UPLOAD_DIRECTORY + name;
							item.write(new File(Term2OntPath));
						}
					}

				} else if (item.getFieldName().equals("Term1"))
					Term1 = item.getString();

				else if (item.getFieldName().equals("Term2"))
					Term2 = item.getString();

				else if (item.getFieldName().equals("SimICComboBox"))
					Simflag = item.getString();

				else if (item.getFieldName().equals("Term1Source"))
					Term1Source = item.getString();

				else if (item.getFieldName().equals("Term2Source"))
					Term2Source = item.getString();

				else if (item.getFieldName().equals("defaultSetting"))
					defSet = item.getString();

				else if (item.getFieldName().equals("HisIter"))
					Hiter = item.getString();

				else if (item.getFieldName().equals("betaWight"))
					beta = item.getString();

				else if (item.getFieldName().equals("landaWeight"))
					landa = item.getString();

				else if (item.getFieldName().equals("fitThreshold"))
					fitThr = item.getString();

				else if (item.getFieldName().equals("oldFile1"))
					oldFile1 = item.getString().replaceAll("\\\\\\\\", "\\\\");

				else if (item.getFieldName().equals("oldFile2"))
					oldFile2 = item.getString().replaceAll("\\\\\\\\", "\\\\");

				else if (item.getFieldName().equals("DoSim"))
					submitType = "DoSim";

				else if (item.getFieldName().equals("g-recaptcha-response"))
					gRecaptchaResponse = item.getString();

			}

			switch (submitType) {
			case "DoSim":
				if (Term1OntPath == null && !oldFile1.equals(""))
					Term1OntPath = oldFile1;

				if (Term2OntPath == null && !oldFile2.equals(""))
					Term2OntPath = oldFile2;

				if (gRecaptchaResponse == null || "".equals(gRecaptchaResponse)) {
					recaptchaCheck = false;
					request.setAttribute("Term1", Term1);
					request.setAttribute("Term2", Term2);
					request = requestConfig(request, defSet, Hiter, fitThr, landa, beta, Term1Source, Term2Source,
							Simflag, Term1OntPath, Term2OntPath);
					String output = "You missed the Captcha";
					request.setAttribute("res", output.trim());
					request.getRequestDispatcher("/sim.jsp").forward(request, response);

					break;
				}
				if (Term1OntPath != null) {
					String ext1 = FilenameUtils.getExtension(Term1OntPath);
					if (ext1 != "" && !ext1.equals("owl")) {
						request.setAttribute("Term1", Term1);
						request.setAttribute("Term2", Term2);
						String output = "Your 1st selected file is not supported, please select a valid owl file!";
						request.setAttribute("res", output.trim());
						request = requestConfig(request, defSet, Hiter, fitThr, landa, beta, Term1Source, Term2Source,
								Simflag, Term1OntPath, Term2OntPath);
						request.getRequestDispatcher("/sim.jsp").forward(request, response);
						break;
					}
				}
				if (Term2OntPath != null) {
					String ext2 = FilenameUtils.getExtension(Term2OntPath);
					if (ext2 != "" && !ext2.equals("owl")) {
						request.setAttribute("Term1", Term1);
						request.setAttribute("Term2", Term2);
						String output = "Your 2nd selected file is not supported, please select a valid owl file!";
						request.setAttribute("res", output.trim());
						request = requestConfig(request, defSet, Hiter, fitThr, landa, beta, Term1Source, Term2Source,
								Simflag, Term1OntPath, Term2OntPath);
						request.getRequestDispatcher("/sim.jsp").forward(request, response);
						break;
					}
				}
				if (Term1.toLowerCase().trim().equals(Term2.toLowerCase().trim())) {
					res = "1";
					request.setAttribute("Term1", Term1);
					request.setAttribute("Term2", Term2);
					String output = "Similarity of " + Term1 + " and " + Term2 + " is: " + "\n" + res;
					request.setAttribute("res", output.trim());
					request = requestConfig(request, defSet, Hiter, fitThr, landa, beta, Term1Source, Term2Source,
							Simflag, Term1OntPath, Term2OntPath);
					request.getRequestDispatcher("/sim.jsp").forward(request, response);
				} else {
					OntFlag = SimController.DetermineCase(Term1Source, Term2Source, Term1OntPath, Term2OntPath);
					Terms = URIFinder.finder(Term1, Term2, OntFlag, Term1OntPath, Term2OntPath);
					if (!defSet.equals("on"))
						configParm(Hiter, beta, landa, fitThr);
					if (Terms[0] != null && Terms[1] != null) {
						res = SimController.SimBioFunc(Terms[0], Terms[1], OntFlag, Term1OntPath, Term2OntPath,
								Simflag);
						System.out.println("output parameter " + res);
						request.setAttribute("Term1", Term1);
						request.setAttribute("Term2", Term2);
						String output = "Similarity of " + Term1 + " and " + Term2 + " is: " + "\n" + res;
						request.setAttribute("res", output.trim());
						request = requestConfig(request, defSet, Hiter, fitThr, landa, beta, Term1Source, Term2Source,
								Simflag, Term1OntPath, Term2OntPath);
						request.getRequestDispatcher("/sim.jsp").forward(request, response);
					} else {
						String msg = "Your";
						if (Terms[0] == null && Terms[1] == null)
							msg = msg + " Term1 and Term2 cannot be found in the selected ontology.";
						else if (Terms[0] == null)
							msg = msg + " Term1 cannot be found in the selected ontology.";
						else if (Terms[1] == null)
							msg = msg + " Term2 cannot be found in the selected ontology.";
						request.setAttribute("res", msg);
						request.setAttribute("Term1", Term1);
						request.setAttribute("Term2", Term2);
						request = requestConfig(request, defSet, Hiter, fitThr, landa, beta, Term1Source, Term2Source,
								Simflag, Term1OntPath, Term2OntPath);
						request.getRequestDispatcher("/sim.jsp").forward(request, response);
					}
				}

				break;
			}
			System.out.println("Term1: " + Term1 + " Term2: " + Term2);
			Terms = null;
			UPLOAD_DIRECTORY = null;
			Term1 = null;
			Term2 = null;
			Simflag = null;
			OntFlag = null;
			Term1Source = null;
			Term2Source = null;
			Hiter = null;
			beta = null;
			landa = null;
			fitThr = null;
			defSet = null;
			res = null;
			Term1OntPath = null;
			Term2OntPath = null;
			gRecaptchaResponse = null;

		} catch (Exception ex) {
			System.out.println("ex:" + ex);
			PrintWriter out = response.getWriter();
			out.println("<script type=\"text/javascript\">");
			out.println("alert('An exception has occurred during processing. Please try again.');");
			out.println("location='sim.jsp';");
			out.println("</script>");

		}

		t.stop();
		t.elapsedTime();
		System.out.println("timer -------------" + t);
	}

	private HttpServletRequest requestConfig(HttpServletRequest req, String defSet, String hiter, String fitThr,
			String landa, String beta, String Term1Source, String Term2Source, String Simflag, String Term1OntPath,
			String Term2OntPath) {

		if (defSet.equals("on")) {
			req.setAttribute("defCheck", "true");
		} else {
			req.setAttribute("defCheck", "false");
		}

		if (Term1Source.equals("MeSH"))
			req.setAttribute("MeshSel1", "Selected");
		if (Term1Source.equals("SNOMEDCT"))
			req.setAttribute("SnomedSel1", "Selected");
		if (Term1Source.equals("WordNet"))
			req.setAttribute("WordnetSel1", "Selected");
		if (Term1Source.equals("YourFile"))
			req.setAttribute("YourfileSel1", "Selected");

		if (Term2Source.equals("MeSH"))
			req.setAttribute("MeshSel2", "Selected");
		if (Term2Source.equals("SNOMEDCT"))
			req.setAttribute("SnomedSel2", "Selected");
		if (Term2Source.equals("WordNet"))
			req.setAttribute("WordnetSel2", "Selected");
		if (Term2Source.equals("YourFile"))
			req.setAttribute("YourfileSel2", "Selected");

		if (Simflag.equals("Lin"))
			req.setAttribute("LinSel", "Selected");
		if (Simflag.equals("Resnik"))
			req.setAttribute("ResnikSel", "Selected");
		if (Simflag.equals("JC"))
			req.setAttribute("JCSel", "Selected");

		req.setAttribute("histV", hiter);
		req.setAttribute("fitV", fitThr);
		req.setAttribute("landaV", landa);
		req.setAttribute("betaV", beta);

		if (Term1OntPath != null)
			Term1OntPath = Term1OntPath.replaceAll("\\\\", "\\\\\\\\");
		if (Term2OntPath != null)
			Term2OntPath = Term2OntPath.replaceAll("\\\\", "\\\\\\\\");

		req.setAttribute("fileS1", Term1OntPath);
		req.setAttribute("fileS2", Term2OntPath);
		return req;
	}

	private void configParm(String hiter, String beta, String landa, String fitThr) {
		parameters.h = Integer.valueOf(hiter.trim());
		parameters.beta = Double.valueOf(beta.trim());
		parameters.landa = Double.valueOf(landa.trim());
		parameters.fThreshold = Double.valueOf(fitThr.trim());

	}

}
