package com.QuintoTrainee.CineCinco.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.QuintoTrainee.CineCinco.converters.UsuarioConverter;
import com.QuintoTrainee.CineCinco.entities.Usuario;
import com.QuintoTrainee.CineCinco.exceptions.WebException;
import com.QuintoTrainee.CineCinco.models.UsuarioModel;
import com.QuintoTrainee.CineCinco.repositories.UsuarioRepository;

@Service
public class UsuarioService implements UserDetailsService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private UsuarioConverter usuarioConverter;

	public void validar(UsuarioModel usuarioModel, String password, String repeatedPass) throws WebException {

		if (usuarioModel.getAlta() != null) {
			if (usuarioModel.getId() == null || usuarioModel.getId().isEmpty() || usuarioModel.getId().equals("")) {
				throw new WebException("El Id esta vacio");
			}
		}
		if (usuarioModel.getEmail() == null || usuarioModel.getEmail().isEmpty()
				|| usuarioModel.getEmail().equals("")) {
			throw new WebException("El usuario tiene que tener un Email");
		}
		if (usuarioModel.getNombreCompleto() == null || usuarioModel.getNombreCompleto().isEmpty()
				|| usuarioModel.getNombreCompleto().equals("")) {
			throw new WebException("El usuario tiene que tener un nombre completo");
		}
		if (usuarioModel.getUsername() == null || usuarioModel.getUsername().isEmpty()
				|| usuarioModel.getUsername().equals("")) {
			throw new WebException("El usuario tiene que tener un Nombre de Usuario");
		}
		if (usuarioModel.getFechaNacimiento() == null) {
			throw new WebException("El Usuario tiene que tener una fecha de nacimiento");
		}
		if (usuarioModel.getRol() == null) {
			throw new WebException("El Usuario tiene que tener una Rol");
		}
		if (password == null || password.isEmpty() || password.length() <= 8) {
			throw new WebException("Contraseña invalida, debe tener como minimo 8 caracteres");
		}
		if (!password.equals(repeatedPass)) {
			throw new WebException("Las contraseñas deben ser iguales");
		}
	}

	public Usuario guardar(UsuarioModel usuarioModel, String password, String repeatedPass) throws WebException {

		validar(usuarioModel, password, repeatedPass);

		Usuario usuarioEntity = usuarioConverter.modelToEntity(usuarioModel);

		String encriptada = new BCryptPasswordEncoder().encode(password);
		usuarioEntity.setPassword(encriptada);

		if (usuarioEntity.getAlta() != null) {
			usuarioEntity.setModificacion(new Date());
		} else {
			usuarioEntity.setAlta(new Date());
		}

		return usuarioRepository.save(usuarioEntity);
	}

	public void hardDelete(UsuarioModel usuarioModel) throws WebException {
		Usuario usuarioEntity = usuarioConverter.modelToEntity(usuarioModel);
		usuarioRepository.delete(usuarioEntity);
	}

	public Usuario softDelete(UsuarioModel usuarioModel) throws WebException {
		Usuario usuarioEntity = usuarioConverter.modelToEntity(usuarioModel);
		usuarioEntity.setBaja(new Date());
		return usuarioRepository.save(usuarioEntity);
	}

	@Override
	public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {

		Usuario usuario = usuarioRepository.buscarPorMail(mail);

		if (usuario != null) {

			List<GrantedAuthority> permisos = new ArrayList<>();

			GrantedAuthority p1 = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString());
			permisos.add(p1);

			ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			HttpSession session = attr.getRequest().getSession(true);
			session.setAttribute("usuarioSession", usuario);

			User user = new User(usuario.getUsername(), usuario.getPassword(), permisos);

			return user;

		} else {
			return null;
		}
	}

}
