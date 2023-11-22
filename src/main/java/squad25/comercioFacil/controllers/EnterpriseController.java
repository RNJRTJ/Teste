package squad25.comercioFacil.controllers;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import squad25.comercioFacil.models.Employer;
import squad25.comercioFacil.models.Enterprise;
import squad25.comercioFacil.models.MarketPlace;
import squad25.comercioFacil.models.Product;
import squad25.comercioFacil.repositoryes.EmployerRepository;
import squad25.comercioFacil.repositoryes.EnterpriseRepository;
import squad25.comercioFacil.repositoryes.MarketPlaceRepository;
import squad25.comercioFacil.repositoryes.ProductRepository;

@Controller
@RequestMapping("/enterprise")
public class EnterpriseController {
	
	@Autowired
	private EnterpriseRepository enterpriseRepository;
	
	@Autowired
	private EmployerRepository employerRepository;
	
	@Autowired
	private MarketPlaceRepository marketPlaceRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	private Enterprise selectedEnterprise = new Enterprise();
	private MarketPlace selectedMarketPlace = new MarketPlace();
	
	@GetMapping("/listarAll")
	public ModelAndView listarAll() {
		ModelAndView modelAndView = new ModelAndView("employer/enterprise/restrictedArea.html");				

		modelAndView.addObject("enterprises", enterpriseRepository.findAll());
		return modelAndView;
	}
	
	@GetMapping("/showFormEnterprise")
	public ModelAndView showFormEnterprise() {
		ModelAndView modelAndView = new ModelAndView("employer/enterprise/formEnterprise.html");	
		
		this.selectedEnterprise = new Enterprise();
		this.selectedMarketPlace = new MarketPlace();
		
		modelAndView.addObject("marketPlace", this.selectedMarketPlace);
		modelAndView.addObject("enterprise", this.selectedEnterprise);
		return modelAndView;
	}
	
	@GetMapping("/showFormProduct")
	public ModelAndView showFormProduct() {
		ModelAndView modelAndView = new ModelAndView("employer/enterprise/product/formProduct.html");	
		
		modelAndView.addObject("product", new Product());

		return modelAndView;
	}
	
	@GetMapping("/findMarketplace")
	public String findMarketplace(@ModelAttribute("marketPlace") MarketPlace marketPlace, Model model) {
		this.selectedMarketPlace = this.marketPlaceRepository.findBynameMarket(marketPlace.getNameMarket());
		
		if(this.selectedMarketPlace == null) {
			this.selectedMarketPlace = new MarketPlace();
			this.selectedEnterprise = new Enterprise();
			this.selectedMarketPlace.setNameMarket("Não encontrado");
		}

		model.addAttribute("marketPlace", this.selectedMarketPlace);
		model.addAttribute("enterprise", this.selectedEnterprise);
		
		return "employer/enterprise/formEnterprise";
	}
	
	@GetMapping("/findEnterprise")
	public String findEnterprise(@ModelAttribute("enterprise") Enterprise enterprise, Model model) {
		this.selectedEnterprise = this.enterpriseRepository.findBynumStore
				(String.valueOf(enterprise.getNumStore()), this.selectedMarketPlace.getIdMarket());
		
		if(this.selectedEnterprise == null) {
			this.selectedEnterprise = new Enterprise();
		}

		model.addAttribute("enterprise", this.selectedEnterprise);
		model.addAttribute("marketPlace", this.selectedMarketPlace);
		
		return "employer/enterprise/formEnterprise";
	}
	
	@PostMapping("/atualizarPreRegistro")
	public ModelAndView atualizarRegistroEnterprise(@ModelAttribute("enterprise") Enterprise enterprise, @RequestParam("image") MultipartFile fileImage){	
		ModelAndView modelAndview = new ModelAndView("redirect:/enterprise/listarProducts");	
		this.selectedEnterprise.setDescription(enterprise.getDescription());
		
		try {
			this.selectedEnterprise.setImagem(fileImage.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.enterpriseRepository.save(this.selectedEnterprise);
		
		return modelAndview;
	}

	@GetMapping("/imagem/{id}")
	@ResponseBody
	public byte[] exibirImagen(@PathVariable("id") Long id) {
		Enterprise enterprise = this.enterpriseRepository.getReferenceById(id);

		return enterprise.getImagem();
	}
	
	@GetMapping("/listarProducts")
	public ModelAndView listarProducts() {
		ModelAndView modelAndview = new ModelAndView("employer/enterprise/product/listProduct.html");	
		List<Product> arrProducts = this.productRepository.findAllByIdEnterprise(this.selectedEnterprise.getIdEnterprise());
		
		modelAndview.addObject("products", arrProducts);
		modelAndview.addObject("enterprise", this.selectedEnterprise);
		
		return modelAndview;
	}
	
	@PostMapping("/cadastrarProduct")
	public String cadastrarProduct(@ModelAttribute("product") Product produto) {
		produto.setEnterprise(this.selectedEnterprise);
		this.productRepository.save(produto);
		
		return "redirect:/enterprise/listarProducts";
	}

	//CONTROLLER diretamente relacionado ao front acima;
	
	@GetMapping("{id}/cadastrar")
	public ModelAndView cadastrar(@PathVariable("id") Long id) {
		Enterprise enterprise = new Enterprise();
		
		List<Employer> employers = employerRepository.findAll(); 		
		ModelAndView modelAndView = new ModelAndView("employer/enterprise/cadastro.html");	
		modelAndView.addObject("enterprise", enterprise);
		modelAndView.addObject("employers", employers);
		return modelAndView;
	}	
	
	@GetMapping("{id}/listar")
	public ModelAndView listar(@PathVariable("id") Long id) {
		Employer employer = employerRepository.getReferenceById(id);
		ModelAndView modelAndView = new ModelAndView("employer/enterprise/listar.html");				
		List<Enterprise> enterprises = enterpriseRepository.findAllByIdUser(id);

		modelAndView.addObject("enterprises", enterprises);
		modelAndView.addObject("employer", employer);
		return modelAndView;
	}
	
	
	@PostMapping("{id}/cadastrar")
	public ModelAndView cadastrar(@PathVariable("id") Long id, @ModelAttribute Enterprise enterprise,  @RequestParam("idUser") Long idUser){				
		Employer employer = employerRepository.getReferenceById(idUser);
		
		enterprise.setUser(employer);
		ModelAndView modelAndview = new ModelAndView("redirect:/employer/{id}/detalhes");
		enterpriseRepository.save(enterprise);
		return modelAndview;
	}
		
	@GetMapping("/{id}/excluir")
	public ModelAndView excluir(@PathVariable Long id) {
		ModelAndView modelAndView = new ModelAndView("redirect:/employer");
		
		enterpriseRepository.deleteById(id);
		return modelAndView;						
	}
}