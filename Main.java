package paq;

import java.util.*;
import java.sql.*;

public class Main {

	static Scanner scLine = new Scanner(System.in);
	static Scanner scInt = new Scanner(System.in);
	static Scanner scDouble = new Scanner(System.in);
	private static final String URL = "jdbc:sqlite:clinica.db";

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try (Connection conn = DriverManager.getConnection(URL)) {

			System.out.println("Conexión establecida correctamente");

			String sql = """
					CREATE TABLE IF NOT EXISTS pacientes (
					id INTEGER PRIMARY KEY AUTOINCREMENT,
					dni TEXT NOT NULL UNIQUE,
					nombre TEXT NOT NULL,
					edad INTEGER NOT NULL,
					numMovil TEXT,
					peso REAL NOT NULL,
					asegurado INTEGER NOT NULL
					);
					""";

			try (Statement stmt = conn.createStatement()) {

				stmt.execute(sql);

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println("Hay un error en la sentencia SQL con la que creamos la tabla");
			}

			boolean salir = false;

			do {
				System.out.println("\n---GESTIÓN DE PACIENTES---");
				System.out.println("1.-Añadir paciente");
				System.out.println("2.-Listar todos los pacientes");
				System.out.println("3.-Buscar paciente");
				System.out.println("4.-Actualizar paciente");
				System.out.println("5.-Eliminar paciente");
				System.out.println("6.-Listar pacientes asegurados");
				System.out.println("7.-Listar pacientes mayores de edad");
				System.out.println("8.-Listar pacientes en un rango de peso");
				System.out.println("9.-Poner móvil si no lo tiene");
				System.out.println("0.-Salir");
				System.out.println("Elige una opción: ");
				int opcion = scInt.nextInt();

				switch (opcion) {

				case 1:
					aniadirPaciente(conn);
					break;

				case 2:
					listarPacientes(conn);
					break;

				case 3:
					buscarPaciente(conn);
					break;

				case 4:
					actualizarPaciente(conn);
					break;

				case 5:
					eliminarPaciente(conn);
					break;

				case 6:
					listarPacientesAsegurados(conn);
					break;

				case 7:
					listarPacienteMayorEdad(conn);
					break;

				case 8:
					listarPacienteRango(conn);
					break;

				case 9:
					aniadirMovil(conn);
					break;

				case 0:
					System.out.println("Saliendo del gestor...");
					salir = true;
					break;
				}

			} while (!salir);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("No se ha podido establecer la conexión");
		}

	}

	private static void aniadirMovil(Connection conn) {
		// TODO Auto-generated method stub

		System.out.println("\nIntroduce un DNI:");
		String dniAct = scLine.nextLine();

		String sql = "SELECT * FROM pacientes WHERE dni LIKE ?";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, dniAct);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {

				String movilActual = rs.getString("numMovil");

				if (movilActual.isEmpty()) {

					System.out.println("Introduce número de móvil: ");
					String numNuevo = scLine.nextLine();

					String sql2 = "UPDATE pacientes SET numMovil = ? WHERE dni LIKE ?";

					try (PreparedStatement pstmt2 = conn.prepareStatement(sql2)) {

						pstmt2.setString(1, numNuevo);

						pstmt2.setString(2, dniAct);

						int filas = pstmt2.executeUpdate();

						System.out.println("Se han actualizado " + filas + " filas");
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						System.out.println("Error en la actualización");
					}

				} else {

					System.out.println("Ya tiene móvil " + movilActual);

				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Error en la búsqueda");
		}

		System.out.println("\nPulsa enter para continuar");
		scLine.nextLine();

	}

	private static void listarPacienteRango(Connection conn) {
		// TODO Auto-generated method stub
		System.out.println("Introduce un peso mínimo para buscar: ");
		double pesoMin = scDouble.nextDouble();

		System.out.println("Introduce un peso máximo para buscar: ");
		double pesoMax = scDouble.nextDouble();

		String sql = "SELECT * FROM pacientes WHERE peso BETWEEN ? AND ?";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setDouble(1, pesoMin);
			pstmt.setDouble(2, pesoMax);

			ResultSet rs = pstmt.executeQuery();

			boolean encontrado = false;
			while (rs.next()) {
				encontrado = true;
				String asegurado;
				if (rs.getInt("asegurado") == 1) {
					asegurado = "Tiene seguro médico";
				} else {
					asegurado = "No tiene seguro médico";
				}
				System.out.println(rs.getInt("Id") + " | " + rs.getString("dni") + " | " + rs.getString("nombre")
						+ " | " + rs.getInt("edad") + " | " + rs.getString("numMovil") + " | " + rs.getDouble("peso")
						+ " | " + asegurado);
			}
			if (!encontrado) {
				System.out.println("No hay pacientes mayores de edad");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Error en la lectura");
		}

		System.out.println("\nPulsa enter para continuar");
		scLine.nextLine();

	}

	private static void listarPacienteMayorEdad(Connection conn) {
		// TODO Auto-generated method stub

		System.out.println("LISTADO DE PACIENTES MAYORES DE EDAD:");

		String sql = "SELECT * FROM pacientes WHERE edad >= 18";

		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

			boolean encontrado = false;
			while (rs.next()) {
				encontrado = true;
				String asegurado;
				if (rs.getInt("asegurado") == 1) {
					asegurado = "Tiene seguro médico";
				} else {
					asegurado = "No tiene seguro médico";
				}
				System.out.println(rs.getInt("Id") + " | " + rs.getString("dni") + " | " + rs.getString("nombre")
						+ " | " + rs.getInt("edad") + " | " + rs.getString("numMovil") + " | " + rs.getDouble("peso")
						+ " | " + asegurado);
			}
			if (!encontrado) {
				System.out.println("No hay pacientes mayores de edad");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Error en la lectura");
		}

		System.out.println("\nPulsa enter para continuar");
		scLine.nextLine();

	}

	private static void listarPacientesAsegurados(Connection conn) {
		// TODO Auto-generated method stub

		System.out.println("LISTADO DE PACIENTES ASEGURADOS:");

		String sql = "SELECT * FROM pacientes WHERE asegurado = 1";

		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

			boolean encontrado = false;
			while (rs.next()) {
				encontrado = true;
				String asegurado;
				if (rs.getInt("asegurado") == 1) {
					asegurado = "Tiene seguro médico";
				} else {
					asegurado = "No tiene seguro médico";
				}
				System.out.println(rs.getInt("Id") + " | " + rs.getString("dni") + " | " + rs.getString("nombre")
						+ " | " + rs.getInt("edad") + " | " + rs.getString("numMovil") + " | " + rs.getDouble("peso")
						+ " | " + asegurado);
			}
			if (!encontrado) {
				System.out.println("No hay pacientes asegurados");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Error en la lectura");
		}

		System.out.println("\nPulsa enter para continuar");
		scLine.nextLine();

	}

	private static void eliminarPaciente(Connection conn) {
		// TODO Auto-generated method stub

		System.out.println("\nIntroduce el DNI del paciente a eliminar: ");
		String dniElim = scLine.nextLine();

		String sql2 = "SELECT * FROM pacientes WHERE dni LIKE ?";

		try (PreparedStatement pstmt = conn.prepareStatement(sql2)) {

			pstmt.setString(1, dniElim);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {

				String sqlD = "DELETE FROM pacientes WHERE dni LIKE ?";

				try (PreparedStatement pstmt2 = conn.prepareStatement(sqlD)) {

					pstmt2.setString(1, dniElim);

					System.out.println("Seguro de que quiere eliminar? (s/n)");
					String eliminar = scLine.nextLine();

					if (eliminar.equalsIgnoreCase("s")) {
						int filasAfectadas = pstmt2.executeUpdate();
						System.out.println("\nSe ha eliminado " + filasAfectadas + " paciente");
					} else {
						System.out.println("No se ha eliminado al paciente");
					}

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					System.out.println("Error al intentar eliminar paciente");
				}

			} else {
				System.out.println("DNI no encontrado");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Error en la búsqueda");
		}

		System.out.println("\nPulsa enter para continuar");
		scLine.nextLine();

	}

	private static void actualizarPaciente(Connection conn) {
		// TODO Auto-generated method stub

		System.out.println("\nIntroduce el DNI del registro a modificar:");
		String dniAct = scLine.nextLine();

		String sql = "SELECT * FROM pacientes WHERE dni LIKE ?";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, dniAct);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {

				String nombreActual = rs.getString("nombre");
				int edadActual = rs.getInt("edad");
				String movilActual = rs.getString("numMovil");
				double pesoActual = rs.getDouble("peso");
				int seguroActual = rs.getInt("asegurado");

		
				System.out.println("Nombre: " + nombreActual);
				System.out.println("Nuevo nombre? (enter para mantener): ");
				String nombreNuevo = scLine.nextLine();
				if (nombreNuevo.equals(""))
					nombreNuevo = nombreActual;

				System.out.println("Edad: " + edadActual);
				System.out.println("Nueva edad? (enter para mantener): ");
				String edadNueva = scLine.nextLine();
				int edadN;
				if (edadNueva.equals("")) {
					edadN = edadActual;
				} else {
					edadN = Integer.parseInt(edadNueva);
				}

				System.out.println("Número de móvil: " + movilActual);
				System.out.println("Nuevo número? (enter para mantener): ");
				String numNuevo = scLine.nextLine();
				if (numNuevo.equals("")) {
					numNuevo = movilActual;
				}

				System.out.println("Peso: " + pesoActual);
				System.out.println("Nuevo peso? (enter para mantener): ");
				String pesoNuevo = scLine.nextLine();
				double pesoN;

				if (pesoNuevo.equals("")) {
					pesoN = pesoActual;
				} else {
					pesoN = Double.parseDouble(pesoNuevo);
				}
				

				System.out.println("Seguro médico (1= tiene, 0= no tiene): " + seguroActual);
				System.out.println("Seguro médico? (enter para mantener): ");
				String seguroNuevo = scLine.nextLine();
				int seguroN;

				if (seguroNuevo.equals("")) {
					seguroN = seguroActual;
				} else {
					seguroN = Integer.valueOf(seguroNuevo);
				}

				String sql2 = "UPDATE pacientes SET nombre = ?, edad  = ?, numMovil= ?,  peso = ?,  asegurado = ? WHERE dni LIKE ?";

				try (PreparedStatement pstmt2 = conn.prepareStatement(sql2)) {

					pstmt2.setString(1, nombreNuevo);
					pstmt2.setInt(2, edadN);
					pstmt2.setString(3, numNuevo);
					pstmt2.setDouble(4, pesoN);
					pstmt2.setInt(5, seguroN);
					pstmt2.setString(6, dniAct);

					int filas = pstmt2.executeUpdate();

					System.out.println("Se han actualizado " + filas + " filas");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					System.out.println("Error en la actualización");
				}

			} else {
				System.out.println("No existe paciente con ese dni");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Error en la búsqueda");
		}

		System.out.println("\nPulsa enter para continuar");
		scLine.nextLine();

	}

	private static void buscarPaciente(Connection conn) {
		// TODO Auto-generated method stub

		System.out.println("1.-Buscar por DNI");
		System.out.println("2.-Buscar por nombre ");
		System.out.println("Elige una opción: ");
		int opcion = scInt.nextInt();

		switch (opcion) {
		case 1:

			System.out.println("Introduce el DNI a buscar:");
			String dniBusq = scLine.nextLine();
	
			String sql = "SELECT * FROM pacientes WHERE dni LIKE ?";

			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

				pstmt.setString(1, dniBusq);
				ResultSet rs = pstmt.executeQuery();

				if (rs.next()) {

					String asegurado;
					if (rs.getInt("asegurado") == 1) {
						asegurado = "Tiene seguro médico";
					} else {
						asegurado = "No tiene seguro médico";
					}
					System.out.println(rs.getInt("Id") + " | " + rs.getString("dni") + " | " + rs.getString("nombre")
							+ " | " + rs.getInt("edad") + " | " + rs.getString("numMovil") + " | " + rs.getDouble("peso")
							+ " | " + asegurado);
				} else {
					System.out.println("Paciente no encontrado");
				}

			}

			catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println("Error en la búsqueda");
			}
			break;

		case 2:

			System.out.println("Dime el nombre del paciente a buscar: ");
			String nombreBusq = scLine.nextLine();

			String sql2 = "SELECT * FROM pacientes WHERE nombre LIKE ?";

			try (PreparedStatement pstmt = conn.prepareStatement(sql2)) {

				pstmt.setString(1, "%" + nombreBusq + "%");
				ResultSet rs = pstmt.executeQuery();

				boolean encontrado = false;

				while (rs.next()) {
					encontrado = true;
					String asegurado;
					if (rs.getInt("asegurado") == 1) {
						asegurado = "Tiene seguro médico";
					} else {
						asegurado = "No tiene seguro médico";
					}
					System.out.println(rs.getInt("Id") + " | " + rs.getString("dni") + " | " + rs.getString("nombre")
							+ " | " + rs.getInt("edad") + " | " + rs.getString("numMovil") + " | " + rs.getDouble("peso")
							+ " | " + asegurado);
				}
				if (!encontrado) {
					System.out.println("Paciente no encontrado");
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println("Error en la búsqueda");
			}
			break;

		}

		System.out.println("\nPulsa enter para continuar");
		scLine.nextLine();

	}

	private static void listarPacientes(Connection conn) {
		// TODO Auto-generated method stub

		String sql = "SELECT * FROM pacientes";

		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {

				String asegurado;
				if (rs.getInt("asegurado") == 1) {
					asegurado = "Tiene seguro médico";
				} else {
					asegurado = "No tiene seguro médico";
				}

				System.out.println(rs.getInt("Id") + " | " + rs.getString("dni") + " | " + rs.getString("nombre")
						+ " | " + rs.getInt("edad") + " | " + rs.getString("numMovil") + " | " + rs.getDouble("peso")
						+ " | " + asegurado);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Error en el listado");
		}

		System.out.println("\nPulsa enter para continuar");
		scLine.nextLine();

	}

	private static void aniadirPaciente(Connection conn) {
		// TODO Auto-generated method stub

		System.out.println("Introduce dni:");
		String dni = scLine.nextLine();
		dni=dni.toUpperCase();

		System.out.println("Introduce tu nombre: ");
		String nombre = scLine.nextLine();

		System.out.println("Introduce tu edad: ");
		int edad = scInt.nextInt();

		System.out.println("Introduce tu móvil: ");
		String numMovil = scLine.nextLine();

		System.out.println("Introduce tu peso: ");
		double peso = scDouble.nextDouble();

		System.out.println("Seguro médico (1 = tiene seguro, 0 = no tiene seguro): ");
		int seguro = scInt.nextInt();

		while (seguro != 0 && seguro != 1) {
			System.out.println("Valor no válido. Introduce 1 (disponible) o 0 (no disponible): ");
			seguro = scInt.nextInt();
		}

		String sql = "INSERT INTO pacientes(dni, nombre, edad, numMovil, peso, asegurado) VALUES(?, ?, ?, ?, ?, ?)";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, dni);
			pstmt.setString(2, nombre);
			pstmt.setInt(3, edad);
			pstmt.setString(4, numMovil);
			pstmt.setDouble(5, peso);
			pstmt.setInt(6, seguro);
			pstmt.executeUpdate();

			System.out.println("Paciente " + nombre + " añadido");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Error al añadir paciente");
		}

		System.out.println("\nPulsa enter para continuar");
		scLine.nextLine();

	}

}
