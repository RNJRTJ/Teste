package squad25.comercioFacil.repositoryes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import squad25.comercioFacil.models.MarketPlace;


@Repository
public interface MarketPlaceRepository  extends JpaRepository<MarketPlace, Long> {
	 @Query
	 (value = "SELECT * FROM market_place WHERE name_market = :nameMarket", nativeQuery = true)
	 public MarketPlace findBynameMarket(String nameMarket); 
}