package com.javadapur.controller;

import com.javadapur.model.Recipe;
import com.javadapur.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private RecipeRepository recipeRepository;

    // Menampilkan dashboard admin
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        List<Recipe> unconfirmedRecipes = recipeRepository.findByConfirmed(false);
        List<Recipe> confirmedRecipes = recipeRepository.findByConfirmed(true); // resep yang sudah dikonfirmasi

        model.addAttribute("unconfirmedRecipes", unconfirmedRecipes);
        model.addAttribute("confirmedRecipes", confirmedRecipes);
        return "admin_dashboard";
    }

    // Konfirmasi resep
    @PostMapping("/confirm/{id}")
    public String confirmRecipe(@PathVariable Long id) {
        Recipe recipe = recipeRepository.findById(id).orElse(null);
        if (recipe != null) {
            recipe.setConfirmed(true);
            recipeRepository.save(recipe);
        }
        return "redirect:/admin/dashboard";
    }

    // Hapus resep
    @PostMapping("/delete/{id}")
    public String deleteRecipe(@PathVariable Long id) {
        Recipe recipe = recipeRepository.findById(id).orElse(null);
        if (recipe != null) {
            recipeRepository.deleteById(id); // Menghapus resep berdasarkan ID
        }
        return "redirect:/admin/dashboard"; // Kembali ke dashboard admin setelah hapus
    }
}
