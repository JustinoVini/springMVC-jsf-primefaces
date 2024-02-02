package br.com.project.model.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.sun.istack.NotNull;

import br.com.project.annotation.IdentificaCampoPesquisa;

//import com.sun.istack.internal.NotNull;

@Audited
@Entity
@Table(name = "estado")
@SequenceGenerator(name = "estado_seq", sequenceName = "estado_seq", initialValue = 1, allocationSize = 1)
public class Estado implements Serializable {

	private static final long serialVersionUID = 2761222955627513137L;

	@IdentificaCampoPesquisa(descricaoCampo = "Código", campoConsulta = "est_id")
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "estado_seq")
	private Long est_id;

	@Column(length = 10, nullable = true)
	private String est_uf;

	@IdentificaCampoPesquisa(descricaoCampo = "Nome", campoConsulta = "est_nome", principal = 1)
	@Column(length = 100, nullable = false)
	private String est_nome;

	@NotAudited 
	@OneToMany(mappedBy = "estado", orphanRemoval = false)
	@Cascade(value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
	private List<Cidade> cidades = new ArrayList<Cidade>(); 

	@Basic 
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "pais")
	@NotNull
	@ForeignKey(name = "pais_fk")
	private Pais pais = new Pais();
	
	@Version
	@Column(name = "versionNum")
	private int versionNum;

}
