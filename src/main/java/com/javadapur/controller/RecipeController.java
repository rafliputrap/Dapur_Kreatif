package com.javadapur.controller;

import com.javadapur.model.Recipe;
import com.javadapur.repository.RecipeRepository;
import com.javadapur.service.FileUploadService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class RecipeController {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private FileUploadService fileUploadService;

    // Menampilkan semua resep yang sudah dikonfirmasi
    @GetMapping("/")
    public String index(Model model) {
        List<Recipe> recipes = recipeRepository.findByConfirmed(true);
        model.addAttribute("recipes", recipes);
        return "index";
    }

    // Form tambah resep
    @GetMapping("/add")
    public String addRecipeForm(@RequestParam(value = "error", required = false) String error, Model model) {
        model.addAttribute("recipe", new Recipe());
        if (error != null) {
            model.addAttribute("error", getErrorMessage(error)); // Menampilkan pesan error di UI
        }
        return "add_recipe";
    }

    // Proses submit resep baru
    @PostMapping("/add")
    public String addRecipeSubmit(@ModelAttribute Recipe recipe,
            @RequestParam("photoFile") MultipartFile photoFile) {
        // Validasi jika file kosong
        if (photoFile.isEmpty()) {
            return "redirect:/add?error=photoEmpty"; // Menampilkan pesan error jika file kosong
        }

        // Validasi ekstensi file (hanya gambar)
        String contentType = photoFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return "redirect:/add?error=invalidFileType"; // Menampilkan error jika bukan file gambar
        }

        try {
            // Mengupload foto menggunakan service
            String photoPath = fileUploadService.uploadPhoto(photoFile, "uploads");
            recipe.setPhotoPath(photoPath); // Menyimpan path foto yang di-upload

            // Mengatur status default resep yang belum dikonfirmasi admin
            recipe.setConfirmed(false);

            // Menyimpan resep ke database
            recipeRepository.save(recipe);

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/add?error=uploadFailed"; // Menampilkan pesan error jika upload gagal
        }

        return "redirect:/"; // Arahkan ke halaman utama setelah submit
    }

    // Fitur search resep
    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        List<Recipe> recipes = recipeRepository.findByTitleContaining(keyword);
        model.addAttribute("recipes", recipes);
        return "index";
    }

    // Detail resep
    @GetMapping("/recipe/{id}")
    public String viewRecipeDetail(@PathVariable Long id, Model model) {
        Recipe recipe = recipeRepository.findById(id).orElse(null);
        if (recipe == null) {
            return "redirect:/";
        }
        model.addAttribute("recipe", recipe);
        return "recipe_detail";
    }

    // Metode untuk mendapatkan pesan error yang lebih mudah dipahami
    private String getErrorMessage(String error) {
        switch (error) {
            case "photoEmpty":
                return "File foto tidak boleh kosong!";
            case "invalidFileType":
                return "Hanya gambar yang diizinkan untuk diupload!";
            case "uploadFailed":
                return "Gagal mengupload file, coba lagi!";
            default:
                return "Terjadi kesalahan, coba lagi.";
        }
    }
}
