package br.com.project.bean.geral;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.model.SelectItem;

import org.hibernate.Query;
import org.springframework.stereotype.Component;

import br.com.framework.interfac.crud.InterfaceCrud;
import br.com.project.annotation.IdentificaCampoPesquisa;
import br.com.project.enums.CondicaoPesquisa;
import br.com.project.report.util.BeanReportView;
import br.com.project.util.all.UtilitariaRegex;

@Component
public abstract class BeanManagedViewAbstract extends BeanReportView {

	private static final long serialVersionUID = 1L;

	protected abstract Class<?> getClassImplement();

	protected abstract InterfaceCrud<?> getController();

	public ObjetoCampoConsulta objetoCampoConsultaSelecionado;

	public List<SelectItem> listaCampoPesquisa;

	public List<SelectItem> listaCondicaoPesquisa;

	public CondicaoPesquisa condicaoPesquisaSelecionado;

	public String valorPesquisa;

	public abstract String condicaoAndParaPesquisa() throws Exception;

	public void setValorPesquisa(String valorPesquisa) {
		this.valorPesquisa = valorPesquisa;
	}

	public String getValorPesquisa() {
		return valorPesquisa != null ? new UtilitariaRegex().retiraAcentos(valorPesquisa) : "";
	}

	public void setCondicaoPesquisaSelecionado(CondicaoPesquisa condicaoPesquisaSelecionado) {
		this.condicaoPesquisaSelecionado = condicaoPesquisaSelecionado;
	}

	public CondicaoPesquisa getCondicaoPesquisaSelecionado() {
		return condicaoPesquisaSelecionado;
	}

	public List<SelectItem> getListaCondicaoPesquisa() {
		listaCondicaoPesquisa = new ArrayList<SelectItem>();
		for (CondicaoPesquisa condicaoPesquisa : CondicaoPesquisa.values()) {
			listaCondicaoPesquisa.add(new SelectItem(condicaoPesquisa, condicaoPesquisa.toString()));
		}

		return listaCondicaoPesquisa;
	}

	public ObjetoCampoConsulta getObjetoCampoConsultaSelecionado() {
		return objetoCampoConsultaSelecionado;
	}

	public void setObjetoCampoConsultaSelecionado(ObjetoCampoConsulta objetoCampoConsultaSelecionado) {

		if (objetoCampoConsultaSelecionado != null) {
			for (Field field : getClassImplement().getDeclaredFields()) {
				if (field.isAnnotationPresent(IdentificaCampoPesquisa.class)) {
					if (objetoCampoConsultaSelecionado.getCampoBanco().equalsIgnoreCase(field.getName())) {
						String descricaoCampo = field.getAnnotation(IdentificaCampoPesquisa.class).descricaoCampo();
						objetoCampoConsultaSelecionado.setDescricao(descricaoCampo);
						objetoCampoConsultaSelecionado.setTipoClass(field.getType().getCanonicalName());
						objetoCampoConsultaSelecionado
								.setPrincipal(field.getAnnotation(IdentificaCampoPesquisa.class).principal());
						break;
					}
				}
			}
		}

		this.objetoCampoConsultaSelecionado = objetoCampoConsultaSelecionado;
	}

	public List<SelectItem> getListaCampoPesquisa() {

		listaCampoPesquisa = new ArrayList<SelectItem>();
		List<ObjetoCampoConsulta> listTemp = new ArrayList<ObjetoCampoConsulta>();

		for (Field field : getClassImplement().getDeclaredFields()) {
			if (field.isAnnotationPresent(IdentificaCampoPesquisa.class)) {
				String descricao = field.getAnnotation(IdentificaCampoPesquisa.class).descricaoCampo();
				String descricaoCampoPesquisa = field.getAnnotation(IdentificaCampoPesquisa.class).campoConsulta();
				int isPrincipal = field.getAnnotation(IdentificaCampoPesquisa.class).principal();

				ObjetoCampoConsulta objetoCampoConsulta = new ObjetoCampoConsulta();
				objetoCampoConsulta.setDescricao(descricao);
				objetoCampoConsulta.setCampoBanco(descricaoCampoPesquisa);
				objetoCampoConsulta.setTipoClass(field.getType().getCanonicalName());
				objetoCampoConsulta.setPrincipal(isPrincipal);
				listTemp.add(objetoCampoConsulta);
			}
		}

		orderReverse(listTemp);

		for (ObjetoCampoConsulta objetoCampoConsulta : listTemp) {
			listaCampoPesquisa.add(new SelectItem(objetoCampoConsulta));
		}

		return listaCampoPesquisa;
	}

	private void orderReverse(List<ObjetoCampoConsulta> listTemp) {
		Collections.sort(listTemp, new Comparator<ObjetoCampoConsulta>() {

			@Override
			public int compare(ObjetoCampoConsulta objt1, ObjetoCampoConsulta objt2) {
				return objt1.getPrincipal().compareTo(objt2.getPrincipal());
			}
		});
	}

	public String getSqlLazyQuery() throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append(" select entity from ");
		sql.append(getQueryConsulta());
		sql.append(" order by entity. ");
		sql.append(objetoCampoConsultaSelecionado.getCampoBanco());
		return sql.toString();
	}

	private StringBuilder getQueryConsulta() throws Exception {
		valorPesquisa = new UtilitariaRegex().retiraAcentos(valorPesquisa);
		StringBuilder sql = new StringBuilder();
		sql.append(getClassImplement().getSimpleName());
		sql.append(" entity where ");

		sql.append(" retira_acentos(upper(cast(entity.");
		sql.append(objetoCampoConsultaSelecionado.getCampoBanco());
		sql.append(" as text))) ");

		if (condicaoPesquisaSelecionado.name().equals(CondicaoPesquisa.IGUAL_A.name())) {
			sql.append(" = retira_acentos(upper('");
			sql.append(valorPesquisa);
			sql.append("'))");
		} else if (condicaoPesquisaSelecionado.name().equals(CondicaoPesquisa.CONTEM.name())) {
			sql.append(" like retira_acentos(upper('%");
			sql.append(valorPesquisa);
			sql.append("%'))");
		} else if (condicaoPesquisaSelecionado.name().equals(CondicaoPesquisa.INICIA_COM.name())) {
			sql.append(" like retira_acentos(upper('");
			sql.append(valorPesquisa);
			sql.append("%'))");
		} else if (condicaoPesquisaSelecionado.name().equals(CondicaoPesquisa.TERMINA_COM.name())) {
			sql.append(" like retira_acentos(upper('%");
			sql.append(valorPesquisa);
			sql.append("'))");
		}
		sql.append(" ");
		sql.append(condicaoAndParaPesquisa());
		return sql;
	}

	public int totalRegistroConsulta() throws Exception {
		Query query = getController().obterQuery("select count(entity) from " + getQueryConsulta());
		Number result = (Number) query.uniqueResult();
		return result.intValue();
	}

}
