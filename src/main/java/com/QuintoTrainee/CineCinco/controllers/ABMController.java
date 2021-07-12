package com.QuintoTrainee.CineCinco.controllers;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.QuintoTrainee.CineCinco.enums.Genero;
import com.QuintoTrainee.CineCinco.enums.Idioma;
import com.QuintoTrainee.CineCinco.exceptions.WebException;
import com.QuintoTrainee.CineCinco.models.FuncionModel;
import com.QuintoTrainee.CineCinco.models.PeliculaModel;
import com.QuintoTrainee.CineCinco.models.SalaModel;
import com.QuintoTrainee.CineCinco.services.FuncionService;
import com.QuintoTrainee.CineCinco.services.PeliculaService;
import com.QuintoTrainee.CineCinco.services.SalaService;
import com.QuintoTrainee.CineCinco.utils.UtilDate;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/ABM")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ABMController {

	@Autowired
	private PeliculaService peliculaService;
	
	@Autowired
	private SalaService salaService;
	
	@Autowired
	private FuncionService funcionService;

	// PELICULAS

	@GetMapping("/gestor_peliculas")
	public String gestorPeliculas(ModelMap modelo) {
		try {

			List<PeliculaModel> peliculas = peliculaService.listarPeliculasActivasModels();
			modelo.addAttribute("peliculas", peliculas);

			modelo.addAttribute("pelicula", new PeliculaModel());

			Set<Genero> generos = EnumSet.allOf(Genero.class);
			modelo.put("generos", generos);

		} catch (WebException e) {
			e.printStackTrace();
		}

		return "/TestBack/templates_basicas/peliculasABM.html";
		//return "/TestBack/GestorPelicula.html";
	}

	@PostMapping("/agregarPelicula")
	public String agregarPelicula(ModelMap modelo, @Valid @ModelAttribute("pelicula") PeliculaModel peliculaModel) {

		try {
			peliculaService.guardar(peliculaModel);
			return "redirect:/ABM/gestor_peliculas?estado=EXITO!";
		} catch (Exception ex) {
			modelo.put("error", ex.getMessage());
			return this.gestorPeliculas(modelo);
		}

	}

	@PostMapping("/eliminarPelicula")
	public String eliminarPelicula(ModelMap modelo, @RequestParam String idPelicula) {
		try {
			peliculaService.softDelete(idPelicula);
			return "redirect:/ABM/gestor_peliculas?estado=EXITO!";
		} catch (Exception ex) {
			modelo.put("error", ex.getMessage());
			return this.gestorPeliculas(modelo);
		}
	}

	@PostMapping("/modificarPelicula")
	public String modificarPelicula(ModelMap modelo, @Valid @ModelAttribute("pelicula") PeliculaModel peliculaModel) {
		try {
			peliculaService.guardar(peliculaModel);
			return "redirect:/ABM/gestor_peliculas?estado=EXITO!";
		} catch (Exception ex) {
			modelo.put("error", ex.getMessage());
			return this.gestorPeliculas(modelo);
		}
	}

	// SALAS

	@GetMapping("/gestor_salas")
	public String gestorSalas(ModelMap modelo) {
		
		try {

			List<SalaModel> salas = salaService.listarSalasActivasModels();
			modelo.addAttribute("salas", salas);

			modelo.addAttribute("sala", new SalaModel());

		} catch (WebException e) {
			e.printStackTrace();
		}
		
		return "/TestBack/templates_basicas/salasABM.html";
		//return "/TestBack/GestorSala.html";
	}

	@PostMapping("/agregarSala")
	public String agregarSala(ModelMap modelo, @Valid @ModelAttribute("sala") SalaModel salaModel) {

		try {
			salaService.guardar(salaModel);
			return "redirect:/ABM/gestor_salas?estado=EXITO!";
		} catch (Exception ex) {
			ex.printStackTrace();
			modelo.put("error", ex.getMessage());
			return this.gestorSalas(modelo);
		}

	}

	@PostMapping("/eliminarSala")
	public String eliminarSala(ModelMap modelo, @RequestParam String idSala) {
		try {
			salaService.softDelete(idSala);
			return "redirect:/ABM/gestor_salas?estado=EXITO!";
		} catch (Exception ex) {
			modelo.put("error", ex.getMessage());
			return this.gestorSalas(modelo);
		}
	}

	@PostMapping("/modificarSala")
	public String modificarSala(ModelMap modelo, @Valid @ModelAttribute("sala") SalaModel salaModel) {
		try {
			salaService.guardar(salaModel);
			return "redirect:/ABM/gestor_salas?estado=EXITO!";
		} catch (Exception ex) {
			modelo.put("error", ex.getMessage());
			return this.gestorSalas(modelo);
		}
	}
	
	// FUNCIONES PENDIENTE
	
	@GetMapping("/gestor_funciones")
	public String gestorFunciones(ModelMap modelo) {

		try {

			List<FuncionModel> funciones = funcionService.listarFuncionesActivasModels();
			modelo.addAttribute("funciones", funciones);

			List<PeliculaModel> peliculas = peliculaService.listarPeliculasActivasModels();
			modelo.addAttribute("peliculas", peliculas);
			
			List<SalaModel> salas = salaService.listarSalasActivasModels();
			modelo.addAttribute("salas", salas);
			
			Set<Idioma> idiomas = EnumSet.allOf(Idioma.class);
			modelo.put("idiomas", idiomas);
			
			modelo.addAttribute("funcion", new FuncionModel());
			modelo.addAttribute("peliculaM", new PeliculaModel());
			modelo.addAttribute("salaM", new SalaModel());

		} catch (WebException e) {
			e.printStackTrace();
		}

		return "/TestBack/templates_basicas/funcionesABM.html";
		//return "/TestBack/GestorFuncion.html";
	}
	
	@PostMapping("/agregarFuncion")
	public String agregarFuncion(ModelMap modelo, @Valid @ModelAttribute("funcion") FuncionModel funcionModel, 
			@RequestParam(required=false) String fechaEmision,
			@RequestParam(required=false) String horarioEmision) {

		try {
			
			System.out.println("FUNCION");
			System.out.println(funcionModel.toString());
			System.out.println("FECHA");
			System.out.println(fechaEmision);
			System.out.println("HORARIO");
			System.out.println(horarioEmision);
			System.out.println("Fecha Final:");
			String fechaFinal = fechaEmision +" "+ horarioEmision;
			System.out.println(fechaFinal);
			
			funcionModel.setFecha(UtilDate.parseFechaHoraGuiones(fechaFinal));
			funcionModel.setHorario(UtilDate.parseFechaHoraGuiones(fechaFinal));
			
			System.out.println("FUNCION fecha");
			System.out.println(funcionModel.getFecha());
			System.out.println("FUNCION horario");
			System.out.println(funcionModel.getHorario());
			
			//FALTA PROBAR SI FUNCIONA MAÑANAAAA
			
			//funcionService.guardar(funcionModel);
			return "redirect:/ABM/gestor_funciones?estado=EXITO!";
		} catch (Exception ex) {
			modelo.put("error", ex.getMessage());
			return this.gestorFunciones(modelo);
		}

	}

	@PostMapping("/eliminarFuncion")
	public String eliminarFuncion(ModelMap modelo, @RequestParam String idFuncion) {
		try {
			funcionService.softDelete(idFuncion);
			return "redirect:/ABM/gestor_funciones?estado=EXITO!";
		} catch (Exception ex) {
			modelo.put("error", ex.getMessage());
			return this.gestorFunciones(modelo);
		}
	}

	@PostMapping("/modificarFuncion")
	public String modificarFuncion(ModelMap modelo, @Valid @ModelAttribute("funcion") FuncionModel funcionModel) {
		try {
			funcionService.guardar(funcionModel);
			return "redirect:/ABM/gestor_funciones?estado=EXITO!";
		} catch (Exception ex) {
			modelo.put("error", ex.getMessage());
			return this.gestorFunciones(modelo);
		}
	}
}
