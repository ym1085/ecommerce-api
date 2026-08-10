package com.farmmarket.domain;

import com.farmmarket.common.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Comment("상품에 등록된 이미지 파일 정보")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "TB_PRODUCT_IMAGE",
        uniqueConstraints = @UniqueConstraint(
                name =  "uk_product_image_display_order",
                columnNames = {"product_id", "display_order"}
        )
)
@Entity
public class ProductImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("상품 이미지 PK(예: 100)")
    @Column(name = "product_image_id")
    private Long id;

    @Comment("클라이언트가 조회하는 상품 이미지 URL(예: https://cdn.example.com/products/10/uuid.webp)")
    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Comment("사용자가 업로드한 원본 파일명(예: black-tshirt-front.jpg)")
    @Column(name = "original_file_name", nullable = false, length = 255)
    private String originalFileName;

    @Comment("이미지 파일 형식(예: image/jpeg, image/webp)")
    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Comment("이미지 파일 크기(byte, 예: 204800은 200KB)")
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Comment("상품 대표 이미지 여부(예: Y이면 상품 목록에 노출)")
    @Column(name = "is_representative", nullable = false)
    private String representativeYn;

    @Comment("상품 이미지 노출 순서(예: 1이면 첫 번째 이미지)")
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Comment("이미지가 연결된 상품 PK(예: 10이면 product_id 10 상품)")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Builder
    private ProductImage(
            String imageUrl,
            String originalFileName,
            String contentType,
            Long fileSize,
            String representativeYn,
            Integer displayOrder,
            Product product
    ) {
        this.imageUrl = imageUrl;
        this.originalFileName = originalFileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.representativeYn = representativeYn;
        this.displayOrder = displayOrder;
        this.product = product;
    }

    /**
     * 업로드가 완료된 상품 이미지의 메타데이터를 생성한다
     * imageUrl에는 클라이언트가 상품 이미지를 조회할 때 사용하는 URL을 전달한다
     */
    public static ProductImage create(
            String imageUrl,
            String originalFileName,
            String contentType,
            Long fileSize,
            String representativeYn,
            Integer displayOrder,
            Product product
    ) {
        return ProductImage.builder()
                .imageUrl(imageUrl)
                .originalFileName(originalFileName)
                .contentType(contentType)
                .fileSize(fileSize)
                .representativeYn(representativeYn)
                .displayOrder(displayOrder)
                .product(product)
                .build();
    }
}
