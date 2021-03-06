/**
 * SitNBeer
 * Romain Capocasale, Vincent Moulin and Jonas Freiburghaus
 * He-Arc, INF3dlm-a
 * Spring Course
 * 2019-2020
 */

package ch.hearc.sitnbeer.controllers;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import ch.hearc.sitnbeer.models.Bar;
import ch.hearc.sitnbeer.models.Beer;
import ch.hearc.sitnbeer.models.Order;
import ch.hearc.sitnbeer.models.User;
import ch.hearc.sitnbeer.models.enums.RoleEnum;
import ch.hearc.sitnbeer.repositories.IBarRepository;
import ch.hearc.sitnbeer.repositories.IRoleRepository;
import ch.hearc.sitnbeer.repositories.IUserRepository;
import ch.hearc.sitnbeer.validators.BarAddValidator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import ch.hearc.sitnbeer.services.BarService;
import com.sipios.springsearch.anotation.SearchSpec;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
class BarController {

	@Autowired
	private BarService barService;

	@Autowired
	private BarAddValidator barAddValidator;

	@Autowired
	private IBarRepository barRepository;

	@Autowired
	private IRoleRepository roleRepository;

	@Autowired
	private IUserRepository userRepository;

	// Constantes
	private static final String BARS = "bars";
	private static final String CREATE_BAR = "createBar";
	private static final String UPDATE_BAR = "updateBar";
	private static final String HOME = "home";
	private static final String SHOW_BAR = "showBar";

	@GetMapping("/bars")
	public String index(Model model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) {
		int currentPage = page.orElse(1);
		int pageSize = size.orElse(8);

		Page<Bar> barPage = barService.findPaginated(PageRequest.of(currentPage - 1, pageSize));
		model.addAttribute("barPage", barPage);

		int totalPages = barPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		return BARS;
	}

	/**
	 * Allows you to make a detailed search for bar
	 * 
	 * @param specs Search Criterion
	 * @param model Model Spring Object
	 * @param page  page number
	 * @param size  number of item display on a page
	 * @return template
	 */
	@GetMapping("/bar/query")
	public String searchForBars(@SearchSpec Specification<Bar> specs, Model model,
			@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
		int currentPage = page.orElse(1);
		int pageSize = size.orElse(8);

		Page<Bar> barPage = barService.findPaginatedWithSpecs(PageRequest.of(currentPage - 1, pageSize),
				Specification.where(specs));
		model.addAttribute("barPage", barPage);

		int totalPages = barPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		return BARS;
	}

	@GetMapping("/bar/add")
	public String addBarForm(Model model) {
		model.addAttribute("bar", new Bar());
		return CREATE_BAR;
	}

	@PostMapping("/bar/add")
	public String addBar(@ModelAttribute Bar bar, Model model, BindingResult bindingResult, Principal principal, HttpServletRequest request) throws ServletException{

		User loggedUser = userRepository.findByUsername(principal.getName());

		if (loggedUser.getOwnedBar() != null) {
			return CREATE_BAR;
		}

		barAddValidator.validate(bar, bindingResult);

		if (bindingResult.hasErrors()) {
			return CREATE_BAR;
		}

		bar.setUser(loggedUser);
		barRepository.save(bar);

		loggedUser.setRole(roleRepository.findByRole(RoleEnum.ENTERPRISE.toString()));
		userRepository.save(loggedUser);

		request.logout();

		return "redirect:/home";
	}

	@GetMapping("/bar/{barId}")
	public String showBar(Model model, @PathVariable long barId) {
		Optional<Bar> optionalBar = barRepository.findById(barId);
		Bar bar = null;
		if (optionalBar.isPresent()) {
			bar = optionalBar.get();
		} else {
			return "redirect:/";
		}

		Iterable<Beer> beers = bar.getBeers();

		model.addAttribute("bar", bar);
		model.addAttribute("order", new Order());
		model.addAttribute("beers", beers);

		return SHOW_BAR;
	}

	/**
	 * Update a bar form
	 * 
	 * @param barId Id of a bar
	 * @param model Model Spring object
	 * @return template
	 */
	@GetMapping("/bar/update/{barId}")
	public String updateBarForm(@PathVariable Long barId, Model model) {

		Optional<Bar> optionalBar = barRepository.findById(barId);

		if (optionalBar.isPresent()) {
			model.addAttribute("bar", optionalBar.get());
			return UPDATE_BAR;
		}
		return HOME;
	}

	/**
	 * Allow to update a bar
	 * 
	 * @param id            Id of a bar
	 * @param bar           Bar object to update
	 * @param model
	 * @param bindingResult
	 * @return template
	 */
	@PostMapping("/bar/update/{id}")
	public String updateBeer(@PathVariable Long id, @Valid Bar bar, Model model, BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			bar.setId(id);
			return UPDATE_BAR;
		}

		barRepository.save(bar);
		return HOME;
	}
}