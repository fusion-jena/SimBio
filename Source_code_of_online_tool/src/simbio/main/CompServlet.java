package simbio.main;

/**
 * Comparision Servlet 
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

@WebServlet("/CompServlet")
public class CompServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CompServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
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
			String Term1 = "", Term2 = "", OntPathOld = "", Hiter = "", beta = "", landa = "", fitThr = "", defSet = "",
					MeshCheck = "", SnomedCheck = "", WordnetCheck = "", YourFile = "", LinCheck = "", ResnikCheck = "",
					JCCheck = "", OntPath = null, gRecaptchaResponse = "", oldFile = null;

			boolean recaptchaCheck = false;
			String[][] res = null;
			String UPLOAD_DIRECTORY = getServletContext().getRealPath("/") + "uploads\\";

			List<FileItem> multiFiles = new ServletFileUpload(new DiskFileItemFactory())
					.parseRequest(new ServletRequestContext(request));
			;
			String submitType = "";

			if (!new File(UPLOAD_DIRECTORY).exists())
				new File(UPLOAD_DIRECTORY).mkdir();

			for (FileItem item : multiFiles) {
				if (item.getFieldName().equals("OntPathOld"))
					OntPathOld = item.getString();

				if (item.getFieldName().equals("OntPath")) {
					if (item.getName() != "") {
						String name = new File(item.getName()).getName();
						OntPath = UPLOAD_DIRECTORY + name;
						if (OntPath.equals(""))
							OntPath = OntPathOld;
						item.write(new File(OntPath));
					}

				} else if (item.getFieldName().equals("Term1"))
					Term1 = item.getString();

				else if (item.getFieldName().equals("Term2"))
					Term2 = item.getString();

				else if (item.getFieldName().equals("MeshCheck"))
					MeshCheck = item.getString();

				else if (item.getFieldName().equals("SnomedCheck"))
					SnomedCheck = item.getString();

				else if (item.getFieldName().equals("WordnetCheck"))
					WordnetCheck = item.getString();

				else if (item.getFieldName().equals("YourFile"))
					YourFile = item.getString();

				else if (item.getFieldName().equals("LinCheck"))
					LinCheck = item.getString();

				else if (item.getFieldName().equals("ResnikCheck"))
					ResnikCheck = item.getString();

				else if (item.getFieldName().equals("JCCheck"))
					JCCheck = item.getString();

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

				else if (item.getFieldName().equals("oldFile"))
					oldFile = item.getString().replaceAll("\\\\\\\\", "\\\\");

				else if (item.getFieldName().equals("DoCompare"))
					submitType = "DoCompare";

				else if (item.getFieldName().equals("g-recaptcha-response"))
					gRecaptchaResponse = item.getString();

			}

			switch (submitType) {
			case "DoCompare":
				if (OntPath == null && !oldFile.equals(""))
					OntPath = oldFile;

				if (gRecaptchaResponse == null || "".equals(gRecaptchaResponse)) {
					recaptchaCheck = false;
					request.setAttribute("Term1", Term1);
					request.setAttribute("Term2", Term2);
					String output = "You missed the Captcha!";
					request.setAttribute("res", output.trim());
					request = requestConfig(request, defSet, MeshCheck, SnomedCheck, WordnetCheck, YourFile, LinCheck,
							ResnikCheck, JCCheck, Hiter, fitThr, landa, beta, OntPath);
					request.getRequestDispatcher("/comparison.jsp").forward(request, response);

					break;
				}
				if (OntPath != null) {
					String ext1 = FilenameUtils.getExtension(OntPath);
					if (ext1 != "" && !ext1.equals("owl")) {
						request.setAttribute("Term1", Term1);
						request.setAttribute("Term2", Term2);
						String output = "Your selected file is not supported, please select a valid owl file!";
						request.setAttribute("res", output.trim());
						request = requestConfig(request, defSet, MeshCheck, SnomedCheck, WordnetCheck, YourFile,
								LinCheck, ResnikCheck, JCCheck, Hiter, fitThr, landa, beta, OntPath);
						request.getRequestDispatcher("/comparison.jsp").forward(request, response);
						break;
					}
				}
				if (Term1.toLowerCase().trim().equals(Term2.toLowerCase().trim())) {
					request.setAttribute("Term1", " '" + Term1 + "' ");
					request.setAttribute("Term2", " '" + Term2 + "' ");
					for (int s = 0; s < 16; s++) {
						request.setAttribute("res" + s + "0", "1");
						request.setAttribute("res" + s + "1", "1");
						request.setAttribute("res" + s + "2", "1");
					}
					request.getRequestDispatcher("/comparisonResult.jsp").forward(request, response);

				} else {
					String[][] Terms = URIFinder.finderAll(Term1, Term2, OntPath, MeshCheck, SnomedCheck, WordnetCheck,
							YourFile);
					if (!defSet.equals("on"))
						configParm(Hiter, beta, landa, fitThr);
					res = CompController.CompBioFunc(Terms, OntPath, MeshCheck, SnomedCheck, WordnetCheck, YourFile,
							LinCheck, ResnikCheck, JCCheck);
					System.out.println("output parameter " + res);
					request.setAttribute("Term1", " '" + Term1 + "' ");
					request.setAttribute("Term2", " '" + Term2 + "' ");
					for (int s = 0; s < 16; s++) {
						request.setAttribute("res" + s + "0", res[s][0]);
						request.setAttribute("res" + s + "1", res[s][1]);
						request.setAttribute("res" + s + "2", res[s][2]);
					}
					request.getRequestDispatcher("/comparisonResult.jsp").forward(request, response);
					break;
				}

			}
			System.out.println("Term1: " + Term1 + " Term2: " + Term2);
		} catch (Exception ex) {
			System.out.println("ex:" + ex);
			PrintWriter out = response.getWriter();
			out.println("<script type=\"text/javascript\">");
			out.println("alert('An exception has occurred during processing. Please try again.');");
			out.println("location='comparison.jsp';");
			out.println("</script>");

		}

		t.stop();
		t.elapsedTime();
		System.out.println("timer -------------" + t);

	}

	private void configParm(String hiter, String beta, String landa, String fitThr) {
		parameters.h = Integer.valueOf(hiter.trim());
		parameters.beta = Double.valueOf(beta.trim());
		parameters.landa = Double.valueOf(landa.trim());
		parameters.fThreshold = Double.valueOf(fitThr.trim());

	}

	private HttpServletRequest requestConfig(HttpServletRequest req, String defSet, String meshCheck,
			String snomedCheck, String wordnetCheck, String yourFile, String linCheck, String resnikCheck,
			String jCCheck, String hiter, String fitThr, String landa, String beta, String OntPath) {
		if (defSet.equals("on")) {
			req.setAttribute("defCheck", "true");
		} else {
			req.setAttribute("defCheck", "false");
		}
		if (meshCheck.equals("on")) {
			req.setAttribute("MeshC", "true");
		} else {
			req.setAttribute("MeshC", "false");
		}
		if (snomedCheck.equals("on")) {
			req.setAttribute("SnomedC", "true");
		} else {
			req.setAttribute("SnomedC", "false");
		}
		if (wordnetCheck.equals("on")) {
			req.setAttribute("WordnetC", "true");
		} else {
			req.setAttribute("WordnetC", "false");
		}
		if (yourFile.equals("on")) {
			req.setAttribute("YourfileC", "true");
		} else {
			req.setAttribute("YourfileC", "false");
		}
		if (linCheck.equals("on")) {
			req.setAttribute("LinC", "true");
		} else {
			req.setAttribute("LinC", "false");
		}
		if (resnikCheck.equals("on")) {
			req.setAttribute("ResnikC", "true");
		} else {
			req.setAttribute("ResnikC", "false");
		}
		if (jCCheck.equals("on")) {
			req.setAttribute("JCC", "true");
		} else {
			req.setAttribute("JCC", "false");
		}

		req.setAttribute("histV", hiter);
		req.setAttribute("fitV", fitThr);
		req.setAttribute("landaV", landa);
		req.setAttribute("betaV", beta);

		if (OntPath != null)
			OntPath = OntPath.replaceAll("\\\\", "\\\\\\\\");
		req.setAttribute("fileC", OntPath);

		return req;
	}
}
