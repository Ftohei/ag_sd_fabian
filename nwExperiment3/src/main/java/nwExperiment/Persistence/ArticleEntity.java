package nwExperiment.Persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;


/**
 * Created by fabiankaupmann on 09.04.15.
 */
@Entity
@Table(name = "artikel")
public class ArticleEntity {

    @Id
    @Column(name = "artikelId")
    private byte[] id;

    @Column(name = "lieferantId")
    private Integer lieferantId;

    @Column(name = "quelleId")
    private  String quelleId;

    @Column(name = "artikelpdf")
    private String artikelPdf;

    @Column(name = "herausgeber")
    private String herausgeber;

    @Column(name = "datum")
    private Date datum;

    @Column(name = "titel")
    private String titel;

    @Column(name = "text")
    private String text;

    @Column(name = "ausgabeId")
    private Integer ausgabeId;

    @Column(name = "ressortId")
    private Integer ressortId;

    @Column(name = "autor")
    private String autor;

    @Column(name = "seite")
    private Integer seite;

    public ArticleEntity(){}

    public ArticleEntity(byte[] id, int lieferantId, String quelleId, String artikelPdf, String herausgeber,
                         Date datum, String titel, String text, int ausgabeId, int ressortId, String autor,
                         int seite){
        this.id = id;
        this.lieferantId = lieferantId;
        this.quelleId = quelleId;
        this.artikelPdf = artikelPdf;
        this.herausgeber = herausgeber;
        this.datum = datum;
        this.titel = titel;
        this.text = text;
        this.ausgabeId = ausgabeId;
        this.ressortId = ressortId;
        this.autor = autor;
        this.seite = seite;
    }

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }

    public Integer getLieferantId() {
        return lieferantId;
    }

    public void setLieferantId(Integer lieferantId) {
        this.lieferantId = lieferantId;
    }

    public String getQuelleId() {
        return quelleId;
    }

    public void setQuelleId(String quelleId) {
        this.quelleId = quelleId;
    }

    public String getArtikelPdf() {
        return artikelPdf;
    }

    public void setArtikelPdf(String artikelPdf) {
        this.artikelPdf = artikelPdf;
    }

    public String getHerausgeber() {
        return herausgeber;
    }

    public void setHerausgeber(String herausgeber) {
        this.herausgeber = herausgeber;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getAusgabeId() {
        return ausgabeId;
    }

    public void setAusgabeId(Integer ausgabeId) {
        this.ausgabeId = ausgabeId;
    }

    public Integer getRessortId() {
        return ressortId;
    }

    public void setRessortId(Integer ressortId) {
        this.ressortId = ressortId;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Integer getSeite() {
        return seite;
    }

    public void setSeite(Integer seite) {
        this.seite = seite;
    }
}
