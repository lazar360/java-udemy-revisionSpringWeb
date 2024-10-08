package app.ecommerce.sb_ecom.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Category {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @NonNull
    @NotBlank(message = "La catégorie ne doit pas être vide")
    @Size(min=5, message = "La catégorie ne doit pas avoir une taille inférieure à 5 caractères")
    private String categoryName;
}
