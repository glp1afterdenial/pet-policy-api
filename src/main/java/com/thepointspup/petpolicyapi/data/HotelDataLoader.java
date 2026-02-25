package com.thepointspup.petpolicyapi.data;

import com.thepointspup.petpolicyapi.model.HotelChain;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class HotelDataLoader {

    private final List<HotelChain> hotels = new ArrayList<>(List.of(
        new HotelChain(
            "kimpton",
            "Kimpton Hotels",
            "No weight limit",
            "$0 (free!)",
            false,
            null,
            "The gold standard for dog travel. If your dog fits through the door, they're welcome. No breed restrictions. Every property has a Director of Pet Relations. Wine hour welcomes pups.",
            "Book free with IHG points. Part of IHG One Rewards. The IHG Premier card's 4th night free benefit saves 25% on points stays.",
            "Best in class -- no limits, no fees",
            "excellent",
            "https://www.kimptonhotels.com"
        ),
        new HotelChain(
            "hilton",
            "Hilton",
            "Typically 75 lbs (varies by brand/property)",
            "$50-$95 per stay (varies by brand)",
            false,
            75,
            "All Hilton brands accept pets. Home2 Suites, Homewood Suites, and Embassy Suites are most dog-friendly with more space. No corporate breed restrictions.",
            "Book free with Hilton Honors points -- pay only the pet fee.",
            "Good for most dogs under 75 lbs",
            "good",
            "https://www.hilton.com"
        ),
        new HotelChain(
            "marriott",
            "Marriott",
            "Varies widely (25-100 lbs depending on brand)",
            "$50-$250 per stay (varies significantly)",
            false,
            100,
            "Policies vary by brand and location. TownePlace Suites (up to 100 lbs), Residence Inn, and Element are most pet-friendly. Always call the specific property.",
            "Book free with Marriott Bonvoy points.",
            "Varies widely -- always call ahead",
            "moderate",
            "https://www.marriott.com"
        ),
        new HotelChain(
            "ihg",
            "IHG (Holiday Inn, Staybridge, etc.)",
            "Varies by brand (typically 40-80 lbs)",
            "$25-$100 depending on brand",
            false,
            80,
            "Staybridge Suites (50 lbs) and Candlewood Suites (80 lbs) are most pet-friendly non-Kimpton brands. Holiday Inn policies vary by location.",
            "For large dogs, book Kimpton instead (also IHG).",
            "Moderate -- Kimpton is better for large dogs",
            "moderate",
            "https://www.ihg.com"
        ),
        new HotelChain(
            "hyatt",
            "Hyatt",
            "Typically 50 lbs (Thompson/Andaz may have no limit)",
            "$50-$150 per stay",
            false,
            50,
            "Hyatt Place and Hyatt House are most consistent. Thompson and Andaz are boutique brands with more flexible policies. Always call the specific property.",
            "Book free with Chase points (transfer to World of Hyatt).",
            "Good -- Thompson/Andaz best for large dogs",
            "good",
            "https://www.hyatt.com"
        ),
        new HotelChain(
            "wyndham",
            "Wyndham (La Quinta, etc.)",
            "La Quinta: No weight limit (policies changing)",
            "$0-$25/night at La Quinta",
            false,
            null,
            "La Quinta was famous for no pet fees and no weight limits. Since Wyndham acquired them, some locations have added restrictions. Always call to confirm.",
            "La Quinta is still one of the best budget options for large dogs.",
            "La Quinta is great, call to confirm",
            "good",
            "https://www.wyndhamhotels.com"
        ),
        new HotelChain(
            "bestwestern",
            "Best Western",
            "Up to 80 lbs per dog",
            "$20-$30 per night",
            false,
            80,
            "Most locations allow up to 2 dogs. Policies can vary by property, so call ahead for specific rules.",
            "One of the more consistent mid-range options for larger dogs.",
            "Good for dogs under 80 lbs",
            "good",
            "https://www.bestwestern.com"
        ),
        new HotelChain(
            "choice",
            "Choice Hotels (Comfort Inn, Quality Inn, etc.)",
            "Varies (typically up to 50 lbs)",
            "$20-$50 per night",
            false,
            50,
            "Policies vary significantly by property. Many Choice hotels are franchised, so rules depend on the owner.",
            "Always call the specific location to confirm their pet policy.",
            "Inconsistent -- always call",
            "moderate",
            "https://www.choicehotels.com"
        ),
        new HotelChain(
            "motel6",
            "Motel 6 / Studio 6",
            "No weight limit",
            "$0 at Motel 6, ~$10/night at Studio 6",
            false,
            null,
            "No breed restrictions. One of the most consistently pet-friendly budget chains. Pets must be attended or crated.",
            "Best budget option for large dogs -- no questions asked.",
            "Excellent for all dog sizes",
            "excellent",
            "https://www.motel6.com"
        ),
        new HotelChain(
            "redroof",
            "Red Roof Inn",
            "No weight limit",
            "$0 (free!)",
            false,
            null,
            "No breed restrictions. One well-behaved pet per room. They even have designated pet walk areas at each location.",
            "One of the best budget options -- truly pet-friendly with no fees.",
            "Excellent -- no fees, no limits",
            "excellent",
            "https://www.redroof.com"
        ),
        new HotelChain(
            "drury",
            "Drury Hotels",
            "Up to 80 lbs combined (for multiple pets)",
            "$50 per night",
            false,
            80,
            "Up to 2 pets allowed. Known for excellent customer service and free extras (breakfast, evening drinks).",
            "Great mid-range option with reliable pet policies.",
            "Good for dogs under 80 lbs",
            "good",
            "https://www.druryhotels.com"
        ),
        new HotelChain(
            "loews",
            "Loews Hotels",
            "No weight limit",
            "Varies ($50-$100 per stay)",
            false,
            null,
            "The 'Loews Loves Pets' program provides beds, bowls, treats, and even a room service menu for dogs. Very upscale pet experience.",
            "Splurge-worthy for special trips with your pup.",
            "Excellent -- luxury pet experience",
            "excellent",
            "https://www.loewshotels.com"
        ),
        new HotelChain(
            "extendedstay",
            "Extended Stay America",
            "Up to 2 pets, combined weight under 50 lbs",
            "$25 per night (max $150 per stay)",
            false,
            50,
            "Budget extended-stay chain with kitchens in every room. Good for longer trips. Policies can vary by location -- some franchise locations have different rules.",
            "Good budget option if your dogs are small. Always call to confirm the specific location's policy.",
            "Budget-friendly but strict weight limit",
            "moderate",
            "https://www.extendedstayamerica.com"
        ),
        new HotelChain(
            "sonesta",
            "Sonesta",
            "No weight limit at most properties",
            "$75-$100 per stay",
            false,
            null,
            "Sonesta and Sonesta Simply Suites welcome dogs of all sizes at most locations. Royal Sonesta and other luxury tiers may have restrictions. Call to confirm.",
            "Good option for large dogs when Kimpton isn't available.",
            "Good for large dogs -- call to confirm",
            "good",
            "https://www.sonesta.com"
        ),
        new HotelChain(
            "omni",
            "Omni Hotels",
            "Up to 25 lbs (some locations 50 lbs)",
            "$50-$150 per stay",
            false,
            25,
            "Omni's 'Omni Sensational Pets' program provides beds, bowls, and treats. However, weight limits are strict and vary by property. Luxury chain with higher fees.",
            "Call ahead -- weight limits are strictly enforced at most locations.",
            "Nice amenities but strict weight limits",
            "moderate",
            "https://www.omnihotels.com"
        ),
        new HotelChain(
            "fourseasons",
            "Four Seasons",
            "Varies by property (often no limit)",
            "$0-$50 per stay (many waive fees)",
            false,
            null,
            "Ultra-luxury chain that genuinely welcomes dogs. Many properties have no weight limits and provide luxury pet amenities -- beds, treats, even room service menus for dogs.",
            "If budget allows, this is the most pampered pet experience. Call to confirm specific property policies.",
            "Luxury experience -- often no limits or fees",
            "good",
            "https://www.fourseasons.com"
        )
    ));

    public List<HotelChain> getAllHotels() {
        return hotels;
    }

    public Optional<HotelChain> findById(String id) {
        return hotels.stream()
            .filter(h -> h.getId().equalsIgnoreCase(id))
            .findFirst();
    }

    public boolean existsById(String id) {
        return hotels.stream().anyMatch(h -> h.getId().equalsIgnoreCase(id));
    }

    public void add(HotelChain hotel) {
        hotels.add(hotel);
    }

    public boolean removeById(String id) {
        return hotels.removeIf(h -> h.getId().equalsIgnoreCase(id));
    }
}
