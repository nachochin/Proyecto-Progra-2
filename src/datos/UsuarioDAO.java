/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// Módulo desarrollado por Erika Rojas
package datos;

import entidades.Usuario;
import excepciones.ArchivoInvalidoException;
import excepciones.AutenticacionFallidaException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 ** Clase de acceso a datos para la entidad {@link Usuario}.
 * <p>
 * Lee y escribe el archivo {@code data/usuarios.txt} usando
 * {@link BufferedReader} y {@link BufferedWriter}. Las contraseÃ±as se almacenan
 * como hash SHA-256, nunca en texto plano.
 * </p>
 * <pre>
 *   Formato de lÃ­nea:
 *   nombreUsuario|passwordHash|rol
 *
 *   Ejemplo:
 *   admin|8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918|ADMIN
 * </pre>
 *
 * @author ekaro
 * @version 1.0
 */
public class UsuarioDAO implements ManejadorArchivo<Usuario, String> {

    /**
     * Ruta relativa al archivo de usuarios.
     */
    private static final String RUTA_ARCHIVO = "usuarios.txt";

    /**
     * Separador de campos dentro de cada lÃ­nea.
     */
    private static final String SEPARADOR = "|";

    /**
     * NÃºmero de campos esperados por lÃ­nea.
     */
    private static final int CAMPOS_ESPERADOS = 3;

    // -------------------------------------------------------------------------
    // guardar
    // -------------------------------------------------------------------------
    /**
     * Agrega un nuevo usuario al archivo.
     * <p>
     * La contraseÃ±a recibida en {@code usuario.getPasswordHash()} debe ya venir
     * hasheada. Usar {@link #hashearPassword(String)} antes de llamar este
     * mÃ©todo si se recibe la contraseÃ±a en texto plano.
     * </p>
     *
     * @param usuario Usuario a persistir.
     * @throws ArchivoInvalidoException si ocurre un error al escribir el
     * archivo.
     */
    @Override
    public void guardar(Usuario usuario) throws ArchivoInvalidoException {
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(RUTA_ARCHIVO, true))) {
            bw.write(usuarioALinea(usuario));
            bw.newLine();
        } catch (IOException e) {
            throw new ArchivoInvalidoException(
                    "Error al guardar usuario en " + RUTA_ARCHIVO, e);
        }
    }

    // -------------------------------------------------------------------------
    // buscarPorId
    // -------------------------------------------------------------------------
    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param nombreUsuario Nombre de usuario a buscar.
     * @return El usuario encontrado, o {@code null} si no existe.
     * @throws ArchivoInvalidoException si el archivo tiene un formato invÃ¡lido.
     */
    @Override
    public Usuario buscarPorId(String nombreUsuario) throws ArchivoInvalidoException {
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_ARCHIVO))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }
                Usuario u = lineaAUsuario(linea);
                if (u.getNombreUsuario().equals(nombreUsuario)) {
                    return u;
                }
            }
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            throw new ArchivoInvalidoException(
                    "Error al leer " + RUTA_ARCHIVO, e);
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // listarTodos
    // -------------------------------------------------------------------------
    /**
     * Devuelve todos los usuarios registrados en el sistema.
     *
     * @return Lista de usuarios.
     * @throws ArchivoInvalidoException si alguna lÃ­nea tiene formato invÃ¡lido.
     */
    @Override
    public List<Usuario> listarTodos() throws ArchivoInvalidoException {
        List<Usuario> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_ARCHIVO))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }
                lista.add(lineaAUsuario(linea));
            }
        } catch (FileNotFoundException e) {
            return lista;
        } catch (IOException e) {
            throw new ArchivoInvalidoException(
                    "Error al leer " + RUTA_ARCHIVO, e);
        }
        return lista;
    }

    // -------------------------------------------------------------------------
    // actualizar
    // -------------------------------------------------------------------------
    /**
     * Actualiza los datos de un usuario existente en el archivo.
     *
     * @param usuario Usuario con los datos actualizados.
     * @throws ArchivoInvalidoException si ocurre un error al leer o escribir.
     */
    @Override
    public void actualizar(Usuario usuario) throws ArchivoInvalidoException {
        List<Usuario> lista = listarTodos();
        boolean encontrado = false;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA_ARCHIVO, false))) {
            for (Usuario u : lista) {
                if (u.getNombreUsuario().equals(usuario.getNombreUsuario())) {
                    bw.write(usuarioALinea(usuario));
                    encontrado = true;
                } else {
                    bw.write(usuarioALinea(u));
                }
                bw.newLine();
            }
        } catch (IOException e) {
            throw new ArchivoInvalidoException(
                    "Error al actualizar usuario en " + RUTA_ARCHIVO, e);
        }

        if (!encontrado) {
            throw new ArchivoInvalidoException(
                    "No se encontro el usuario: " + usuario.getNombreUsuario());
        }
    }

    // -------------------------------------------------------------------------
    // eliminar
    // -------------------------------------------------------------------------
    /**
     * Elimina un usuario del archivo por su nombre de usuario.
     *
     * @param nombreUsuario Nombre del usuario a eliminar.
     * @throws ArchivoInvalidoException si ocurre un error al leer o escribir.
     */
    @Override
    public void eliminar(String nombreUsuario) throws ArchivoInvalidoException {
        List<Usuario> lista = listarTodos();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA_ARCHIVO, false))) {
            for (Usuario u : lista) {
                if (!u.getNombreUsuario().equals(nombreUsuario)) {
                    bw.write(usuarioALinea(u));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            throw new ArchivoInvalidoException(
                    "Error al eliminar usuario en " + RUTA_ARCHIVO, e);
        }
    }

    // -------------------------------------------------------------------------
    // autenticar
    // -------------------------------------------------------------------------
    /**
     * Verifica las credenciales de un usuario contra el archivo.
     * <p>
     * Hashea la contraseÃ±a recibida y la compara con el hash almacenado.
     * </p>
     *
     * @param nombreUsuario Nombre de usuario ingresado en el login.
     * @param passwordTextoPlano ContraseÃ±a en texto plano ingresada por el
     * usuario.
     * @return El usuario autenticado si las credenciales son correctas.
     * @throws AutenticacionFallidaException si el usuario no existe o la
     * contraseÃ±a es incorrecta.
     * @throws ArchivoInvalidoException si ocurre un error al leer el archivo.
     */
    public Usuario autenticar(String nombreUsuario, String passwordTextoPlano)
            throws AutenticacionFallidaException, ArchivoInvalidoException {

        Usuario usuario = buscarPorId(nombreUsuario);

        if (usuario == null) {
            throw new AutenticacionFallidaException(
                    "El usuario '" + nombreUsuario + "' no existe en el sistema.");
        }

        String hashIngresado = hashearPassword(passwordTextoPlano);
        if (!hashIngresado.equals(usuario.getPasswordHash())) {
            throw new AutenticacionFallidaException(
                    "Contrasena incorrecta para el usuario: " + nombreUsuario);
        }

        return usuario;
    }

    // -------------------------------------------------------------------------
    // hashearPassword
    // -------------------------------------------------------------------------
    /**
     * Genera el hash SHA-256 de una contraseÃ±a en texto plano.
     * <p>
     * Debe usarse antes de guardar un nuevo usuario y durante la autenticaciÃ³n
     * para comparar la contraseÃ±a ingresada con la almacenada.
     * </p>
     *
     * @param passwordTextoPlano ContraseÃ±a en texto plano a hashear.
     * @return Hash SHA-256 como cadena hexadecimal en minÃºsculas.
     * @throws RuntimeException si el algoritmo SHA-256 no estÃ¡ disponible (no
     * deberÃ­a ocurrir en JVM estÃ¡ndar).
     */
    public String hashearPassword(String passwordTextoPlano) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(
                    passwordTextoPlano.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 no disponible en esta JVM.", e);
        }
    }

    // -------------------------------------------------------------------------
    // MÃ©todos privados de conversiÃ³n
    // -------------------------------------------------------------------------
    /**
     * Convierte un objeto {@link Usuario} a una lÃ­nea de texto con formato
     * pipe.
     *
     * @param u Usuario a convertir.
     * @return LÃ­nea con formato: {@code nombreUsuario|passwordHash|rol}
     */
    private String usuarioALinea(Usuario u) {
        return u.getNombreUsuario() + SEPARADOR
                + u.getPasswordHash() + SEPARADOR
                + u.getRol();
    }

    /**
     * Convierte una lÃ­nea del archivo en un objeto {@link Usuario}.
     *
     * @param linea LÃ­nea leÃ­da del archivo.
     * @return Usuario construido con los datos de la lÃ­nea.
     * @throws ArchivoInvalidoException si la lÃ­nea tiene formato incorrecto.
     */
    private Usuario lineaAUsuario(String linea) throws ArchivoInvalidoException {
        String[] c = linea.split("\\|");
        if (c.length != CAMPOS_ESPERADOS) {
            throw new ArchivoInvalidoException(
                    "Formato invalido en usuarios.txt. Se esperaban "
                    + CAMPOS_ESPERADOS + " campos. LÃ­nea: " + linea);
        }
        return new Usuario(c[0].trim(), c[1].trim(), c[2].trim());
    }
}
