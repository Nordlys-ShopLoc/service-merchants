package fr.nordlys.shoploc.merchants.domain;

import java.util.List;

public interface ShopPort {
    List<Shop> findAll();
}