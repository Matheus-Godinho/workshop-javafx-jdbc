package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {
	
	private Connection connection;

	public SellerDaoJDBC(Connection connection) {
		this.connection = connection;
	}
	
	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department department;
		
		department = new Department();
		department.setId(rs.getInt("DepartmentId"));
		department.setName(rs.getString("DepName"));
		return department;
	}
	private Seller instantiateSeller(ResultSet rs, Department department) throws SQLException {
		Seller seller;
		
		seller = new Seller();
		seller.setId(rs.getInt("Id"));
		seller.setName(rs.getString("Name"));
		seller.setEmail(rs.getString("Email"));
		seller.setBirthDate(rs.getDate("BirthDate"));
		seller.setBaseSalary(rs.getDouble("BaseSalary"));
		seller.setDepartment(department);
		return seller;
	}

	@Override
	public void insert(Seller seller) {
		PreparedStatement ps;
		int updatedRows;
		
		try {
			ps = connection.prepareStatement(
					"INSERT INTO seller "
					+ "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, seller.getName());
			ps.setString(2, seller.getEmail());
			ps.setDate(3, new java.sql.Date(seller.getBirthDate().getTime()));
			ps.setDouble(4, seller.getBaseSalary());
			ps.setInt(5, seller.getDepartment().getId());
			updatedRows = ps.executeUpdate();
			if (updatedRows > 0) {
				ResultSet rs;
				int newId;
				
				rs = ps.getGeneratedKeys();
				if (rs.next()) {
					newId = rs.getInt(1);
					seller.setId(newId);
				}
				DB.closeResultSet();
			} else {
				throw new DbException("Unexpected error! No rows were updated");
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement();
		}
	}

	@Override
	public void update(Seller seller) {
		PreparedStatement ps;
		
		try {
			ps = connection.prepareStatement(
					"UPDATE seller "
					+ "SET Name = ?, Email = ?, BirthDate = ?, "
						+ "BaseSalary = ?, DepartmentId = ? "
					+ "WHERE Id = ?");
			ps.setString(1, seller.getName());
			ps.setString(2, seller.getEmail());
			ps.setDate(3, new java.sql.Date(seller.getBirthDate().getTime()));
			ps.setDouble(4, seller.getBaseSalary());
			ps.setInt(5, seller.getDepartment().getId());
			ps.setInt(6, seller.getId());
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
					"DELETE FROM seller "
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
	public Seller findById(Integer id) {
		PreparedStatement ps;
		ResultSet rs;
		
		try {
			ps = connection.prepareStatement(
					"SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE seller.Id = ?");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				Department department;
				Seller seller;
				
				department = instantiateDepartment(rs);
				seller = instantiateSeller(rs, department);
				return seller;
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
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement ps;
		ResultSet rs;
		List<Seller> sellers;
		Map<Integer, Department> map;
		
		try {
			ps = connection.prepareStatement(
					"SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE DepartmentId = ? "
					+ "ORDER BY Name");
			ps.setInt(1, department.getId());
			rs = ps.executeQuery();
			sellers = new ArrayList<>();
			map = new HashMap<>();
			while (rs.next()) {
				Seller seller;
				
				if (!map.containsKey(rs.getInt("DepartmentId"))) {
					department = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), department);
				}
				department = map.get(rs.getInt("DepartmentId"));
				seller = instantiateSeller(rs, department);
				sellers.add(seller);
			}
			return sellers;
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
	public List<Seller> findAll() {
		PreparedStatement ps;
		ResultSet rs;
		List<Seller> sellers;
		Map<Integer, Department> map;
		
		try {
			ps = connection.prepareStatement(
					"SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "ORDER BY Name");
			rs = ps.executeQuery();
			sellers = new ArrayList<>();
			map = new HashMap<>();
			while (rs.next()) {
				Department department;
				Seller seller;
				
				if (!map.containsKey(rs.getInt("DepartmentId"))) {
					department = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), department);
				}
				department = map.get(rs.getInt("DepartmentId"));
				seller = instantiateSeller(rs, department);
				sellers.add(seller);
			}
			return sellers;
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
