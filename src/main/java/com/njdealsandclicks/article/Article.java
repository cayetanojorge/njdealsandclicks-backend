package com.njdealsandclicks.article;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import com.njdealsandclicks.common.BaseEntity;
import com.njdealsandclicks.product.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.njdealsandclicks.util.StringListToJsonConverterUtil;

import jakarta.persistence.Convert;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "article",
    indexes = {
        @Index(name = "idx_article_public_id", columnList = "public_id"),
        @Index(name = "idx_article_slug", columnList = "slug"),
        @Index(name = "idx_article_published_at", columnList = "published_at")
    }
)
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Article extends BaseEntity {

    @NotBlank
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank
    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    /*
    excerpt è un riassunto breve del contenuto del l’articolo, usato per:
    .anteprime in homepage, feed, o pagine di elenco articoli
    .meta-description per SEO
    .social media sharing (OpenGraph, Twitter cards)
    Esempio:
    Articolo: "Guida ai migliori TV OLED del 2025"
    Excerpt: "Scopri quali TV OLED offrono il miglior rapporto qualità-prezzo: guida aggiornata ai modelli top per film e gaming."
     */
    @Column(name = "excerpt", nullable = true)
    private String excerpt;

    @NotBlank
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content; // in Markdown o HTML

    @Column(name = "image_url", nullable = true)
    private String imageUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter = StringListToJsonConverterUtil.class)
    @Column(name = "tags", columnDefinition = "jsonb", nullable = true)
    private List<String> tags;

    @NotNull
    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = true; // per gestire articoli in bozza, mostrare nel frontend solo quando is_published = true

    /*
    * createdAt     → data di creazione dell'articolo (per tracciamento interno e ordinamento)
    * updatedAt     → data ultima modifica (utile per badge "aggiornato", SEO, controllo versioni)
    * publishedAt   → data di pubblicazione reale (mostrata nel frontend e usata per ordinamento/SEO)
    */
    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = true, updatable = true)
    private ZonedDateTime updatedAt;

    @Column(name = "published_at", nullable = true, updatable = true)
    private ZonedDateTime publishedAt;

    @NotNull
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false; // soft delete come fatto con User
    /*
    "eliminare" un articolo, invece di repository.delete(article), fai:
        article.setIsDeleted(true);
        articleRepository.save(article);
    Filtra nei repository i risultati visibili:
        List<Article> findAllByIsDeletedFalse(); // opp: List<Article> findAllByIsDeletedFalseAndIsPublishedTrue();
     */

    @Column(name = "deleted_at", nullable = true)
    private ZonedDateTime deletedAt;

    @ManyToMany
    @JoinTable(
        name = "article_product",
        joinColumns = @JoinColumn(name = "article_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;


    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now(ZoneId.of("UTC"));
    }
}