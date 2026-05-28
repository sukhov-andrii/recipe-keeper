package nl.inholland.recipekeeper.service;

import nl.inholland.recipekeeper.model.entity.Ingredient;
import nl.inholland.recipekeeper.repository.IngredientRepository;
import nl.inholland.recipekeeper.exception.domain.BusinessRuleViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class IngredientServiceTest {

    @Mock IngredientRepository repository;

    @InjectMocks IngredientService service;

    @Test
    void findOrCreate_should_normalize_and_reuse_existing() {
        Ingredient existing = new Ingredient("tomato");

        when(repository.findByName("tomato"))
                .thenReturn(Optional.of(existing));

        Ingredient result = service.findOrCreate("  Tomato ");

        assertSame(existing, result);
        verify(repository, never()).save(any());
    }

    @Test
    void findOrCreate_should_create_when_missing() {
        when(repository.findByName("tomato"))
                .thenReturn(Optional.empty());

        when(repository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        Ingredient result = service.findOrCreate("Tomato");

        assertEquals("tomato", result.getName());
        verify(repository).save(any());
    }

    @Test
    void findOrCreate_should_reject_blank_or_whitespace() {
        assertThrows(BusinessRuleViolationException.class,
                () -> service.findOrCreate("   "));
    }
}