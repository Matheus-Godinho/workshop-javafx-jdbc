package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao {
	
	private Connection connection;

	public DepartmentDaoJDBC(Connection connection) {
		this.connection = connection;
	}
	
	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department department;
		
		department = new Department();
		department.setId(rs.getInt("Id"));
		department.setName(rs.getString("Name"));
		return department;
	}

	@Override
	public void insert(Department department) {
		PreparedStatement ps;
		int updatedRows;
		
		try {
			ps = connection.prepareStatement(
					"INSERT INTO department "
					+ "(Name) "
					+ "VALUES "
					+ "(?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, department.getName());
			updatedRows = ps.executeUpdate();
			if (updatedRows > 0) {
				ResultSet rs;
				int newId;
				
				rs = ps.getGeneratedKeys();
				if (rs.next()) {					
					newId = rs.getInt(1);
					department.setId(newId);
				}
				DB.closeResultSet();
			} else
				throw new DbException("Unexpected error! No rows were updated");
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement();
		}
	}

	@Override
	public void update(Department department) {
		PreparedStatement ps;
		
		try {
			ps = connection.prepareStatement(
					"UPDATE department "
					+ "SET Name = ? "
					+ "WHERE Id = ?");
			ps.setString(1, department.getName());
			ps.setInt(2, department.getId());
			ps.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement();
		}
		
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement ps;
		
		try {
			ps = connection.prepareStatement(
					"DELETE FROM department "
					+ "WHERE Id = ?");
			ps.setInt(1, id);
			ps.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement();
		}
		
	}

	@Override
	public Department findById(Integer id) {
		PreparedStatement ps;
		ResultSet rs;
		
		try {
			ps = connection.prepareStatement(
					"SELECT department.* "
					+ "FROM department "
					+ "WHERE Id = ?");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				Department department;
				
				department = instantiateDepartment(rs);
				return department;
			}
			return null;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeResultSet();
			DB.closeStatement();
		}
	}

	@Override
	public List<Department> findAll() {
		PreparedStatement ps;
		ResultSet rs;
		List<Department> departments;
		
		try {
			ps = connection.prepareStatement(
					"SELECT * FROM department "
					+ "ORDER BY name");
			rs = ps.executeQuery();
			departments = new ArrayList<>();
			while (rs.next()) {
				Department department;
				
				department = instantiateDepartment(rs);
				departments.add(department);
			}
			return departments;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeResultSet();
			DB.closeStatement();
		}
	}

}
