package com.davidgluzman.couponsys.service.facade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.davidgluzman.couponsys.beans.Category;
import com.davidgluzman.couponsys.beans.Company;
import com.davidgluzman.couponsys.beans.Coupon;
import com.davidgluzman.couponsys.exceptions.AlreadyExistException;
import com.davidgluzman.couponsys.exceptions.InvalidActionException;
import com.davidgluzman.couponsys.exceptions.LoginException;
import com.davidgluzman.couponsys.service.services.CompanyService;
import com.davidgluzman.couponsys.service.services.CouponService;
import com.davidgluzman.couponsys.service.services.CustomerService;

import lombok.Getter;
import lombok.Setter;

@Service
public class CompanyFacade extends ClientFacade {

	@Getter
	@Setter
	private int companyID;

	@Override
	public boolean login(String email, String password) throws LoginException {
		if (!companyService.isCompanyExist(email, password)) {
			throw new LoginException("Company login denied - wrong email or password");
		}
		this.companyID = companyService.getOneCompanyByEmailAndPassword(email, password).getId();
		System.out.println("Company - successful login");
		return true;
	}

	public List<Coupon> getAllCoupons() {
		List<Coupon> coupons = couponService.getAllCouponsByCompanyID(this.companyID);
		return coupons;
	}

	public void addCoupon(Coupon coupon) throws AlreadyExistException {
		List<Coupon> coupons = couponService.getAllCouponsByCompanyID(companyID);
		for (Coupon c : coupons) {
			if (c.getTitle().equalsIgnoreCase(coupon.getTitle())) {
				throw new AlreadyExistException("coupon with the same title already exist");
			}
		}
		couponService.addCoupon(coupon);
	}

	public void updateCoupon(Coupon coupon) throws InvalidActionException {
		Coupon coupon2 = couponService.getOneCoupon(coupon.getId()).get();
		if (coupon2.getCompanyID() != coupon.getCompanyID()) {
			throw new InvalidActionException("changing coupons companyID is not allowed");
		}
		couponService.updateCoupon(coupon);
	}

	public List<Coupon> getAllCouponsByCategory(Category category) {
		List<Coupon> coupons = getAllCoupons();
		List<Coupon> filteredCoupons = new ArrayList<Coupon>();
		for (Coupon c : coupons) {
			if (c.getCategory() == category) {
				filteredCoupons.add(c);
			}
		}
		return filteredCoupons;
	}
	public List<Coupon> getAllCouponsByPriceLessThan(double price){
		List<Coupon> coupons=getAllCoupons();
		List<Coupon> filterCoupons=new ArrayList<Coupon>();
		for (Coupon c : coupons) {
			if (c.getPrice()<=price) {
				filterCoupons.add(c);
			}
		}
		return filterCoupons;
	}
	public Company getCompanyDetails() {
		Company company = companyService.getOneCompany(this.companyID).get();
		company.setCoupons(getAllCoupons());
		return company;
	}
	public void deleteCoupon(int couponID) {
		couponService.deleteCoupon(couponID);
	}
}
