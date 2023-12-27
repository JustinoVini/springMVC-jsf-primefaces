package br.com.project.report.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.util.JRLoader;

@Component
public class ReportUtil implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String UNDERLINE = "_";
	private static final String FOLDER_RELATORIOS = "/relatorios";
	private static final String SUBREPORT_DIR = "SUBREPORT_DIR";
	private static final String EXTENSION_ODS = "ods";
	private static final String EXTENSION_XLS = "xls";
	private static final String EXTENSION_HTML = "html";
	private static final String EXTENSION_PDF = "pdf";
	private String SEPARATOR = File.separator;
	private static final int RELATORIO_PDF = 1;
	private static final int RELATORIO_EXCEL = 2;
	private static final int RELATORIO_HTML = 3;
	private static final int RELATORIO_PLANILHA_OPEN_OFFICE = 4;
	private static final String PONTO = ".";
	private StreamedContent arquivoRetorno = null;
	private String caminhoArquivoRelatorio = null;
	private JRExporter tipoArquivoExportado = null;
	private String extensaoArquivoExportado = "";
	private String caminhoSubreport_dir = "";
	private File arquivoGerado = null;

	public StreamedContent geraRelatorio(List<?> listDataBeanCollectionReport, HashMap parametrosRelatorio,
			String nomeRelatorioJasper, String relatorioSaida, int tipoRelatorio) throws Exception {

		/*
		 * Cria a lista de collectionDataSource de beans que carregam os dados para
		 * relatorio
		 */
		JRBeanCollectionDataSource jrbcds = new JRBeanCollectionDataSource(listDataBeanCollectionReport);

		/*
		 * Fornece o caminho fisico até a pasta de contém os relatórios compilados
		 * .jasper
		 */
		FacesContext context = FacesContext.getCurrentInstance();
		context.responseComplete();
		ServletContext scontext = (ServletContext) context.getExternalContext().getContext();

		String caminhoRelatorio = scontext.getRealPath(FOLDER_RELATORIOS);

		File file = new File(caminhoRelatorio + SEPARATOR + nomeRelatorioJasper + PONTO + "jasper");

		if (caminhoRelatorio == null || (caminhoRelatorio != null && caminhoRelatorio.isEmpty()) || !file.exists()) {
			caminhoRelatorio = this.getClass().getResource(FOLDER_RELATORIOS).getPath();
			SEPARATOR = "";
		}

		/* caminho para imagens */
		parametrosRelatorio.put("REPORT_PARAMETERS_IMG", caminhoRelatorio);

		/* caminho completo até o relatório compilado indicado */
		String caminhoArquivoJasper = caminhoRelatorio + SEPARATOR + nomeRelatorioJasper + PONTO + "jasper";

		/* faz carregamento do relatorio */
		JasperReport relatorioJasper = (JasperReport) JRLoader.loadObjectFromFile(caminhoArquivoJasper);

		/* seta parametro SUBREPORT_DIR como caminho fisico para sub-reports */
		caminhoSubreport_dir = caminhoRelatorio + SEPARATOR;
		parametrosRelatorio.put(SUBREPORT_DIR, caminhoSubreport_dir);

		/* carrega o arquivo compilado para memoria */
		JasperPrint impressora = JasperFillManager.fillReport(relatorioJasper, parametrosRelatorio, jrbcds);

		switch (tipoRelatorio) {
		case RELATORIO_PDF:
			tipoArquivoExportado = new JRPdfExporter();
			extensaoArquivoExportado = EXTENSION_PDF;
			break;
		case RELATORIO_HTML:
			tipoArquivoExportado = new JRHtmlExporter();
			extensaoArquivoExportado = EXTENSION_HTML;
			break;
		case RELATORIO_EXCEL:
			tipoArquivoExportado = new JRXlsExporter();
			extensaoArquivoExportado = EXTENSION_XLS;
			break;
		case RELATORIO_PLANILHA_OPEN_OFFICE:
			tipoArquivoExportado = new JROdtExporter();
			extensaoArquivoExportado = EXTENSION_ODS;
			break;
		default:
			tipoArquivoExportado = new JRPdfExporter();
			extensaoArquivoExportado = EXTENSION_PDF;
			break;
		}

		relatorioSaida += UNDERLINE + DateUtils.getDateAtualReportName();

		/* caminho relatorio exportado */
		caminhoArquivoRelatorio = caminhoRelatorio + SEPARATOR + relatorioSaida + PONTO + extensaoArquivoExportado;

		/* Cria novo file exportado */
		arquivoGerado = new File(caminhoArquivoRelatorio);

		/* prepara a impressão */
		tipoArquivoExportado.setParameter(JRExporterParameter.JASPER_PRINT, impressora);

		/* nome do arquivo fisico a ser impresso/exportado */
		tipoArquivoExportado.setParameter(JRExporterParameter.OUTPUT_FILE, arquivoGerado);

		/* executa a exportação */
		tipoArquivoExportado.exportReport();

		/* remove o arquivo do servidor após ser feito o download pelo usuário */
		arquivoGerado.deleteOnExit();

		/* cria o inputStream para ser usado pelo primefaces */
		InputStream conteudoRelatorio = new FileInputStream(arquivoGerado);

		/* faz o retorno para a aplicação */
		arquivoRetorno = new DefaultStreamedContent(conteudoRelatorio, "application/" + extensaoArquivoExportado,
				relatorioSaida + PONTO + extensaoArquivoExportado);

		return arquivoRetorno;
	}

}
