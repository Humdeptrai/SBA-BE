package sum25.studentcode.backend.modules.Options;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.modules.Options.dto.request.OptionsRequest;
import sum25.studentcode.backend.modules.Options.dto.response.OptionsResponse;
import sum25.studentcode.backend.modules.Options.service.OptionsService;

import java.util.List;

@RestController
@RequestMapping("/api/options")
@RequiredArgsConstructor
public class OptionsController {

    private final OptionsService optionsService;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public OptionsResponse createOption(@RequestBody OptionsRequest request) {
        return optionsService.createOption(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public OptionsResponse getOptionById(@PathVariable Long id) {
        return optionsService.getOptionById(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    public List<OptionsResponse> getAllOptions() {
        return optionsService.getAllOptions();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public OptionsResponse updateOption(@PathVariable Long id, @RequestBody OptionsRequest request) {
        return optionsService.updateOption(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public void deleteOption(@PathVariable Long id) {
        optionsService.deleteOption(id);
    }
}