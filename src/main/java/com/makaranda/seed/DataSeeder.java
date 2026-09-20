package com.makaranda.seed;

import com.makaranda.domain.AppSetting;
import com.makaranda.domain.ClientProfile;
import com.makaranda.domain.Consultation;
import com.makaranda.domain.LearningArticle;
import com.makaranda.domain.User;
import com.makaranda.repo.AppSettingRepository;
import com.makaranda.repo.ClientProfileRepository;
import com.makaranda.repo.ConsultationRepository;
import com.makaranda.repo.LearningArticleRepository;
import com.makaranda.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataSeeder implements CommandLineRunner {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final LearningArticleRepository articles;
    private final AppSettingRepository settings;
    private final ClientProfileRepository clients;
    private final ConsultationRepository bookings;

    public DataSeeder(UserRepository users, PasswordEncoder encoder, LearningArticleRepository articles,
                      AppSettingRepository settings, ClientProfileRepository clients,
                      ConsultationRepository bookings) {
        this.users = users;
        this.encoder = encoder;
        this.articles = articles;
        this.settings = settings;
        this.clients = clients;
        this.bookings = bookings;
    }

    @Override
    public void run(String... args) {
        if (users.count() > 0) return;

        User admin = user("Pandit Rajesh Jha", "admin@makaranda.app", "admin123", User.Role.ADMIN,
                "Platform steward. Mithila Jyotish, Makaranda paddhati.", 28, 0);
        User astro = user("Acharya Sumanth Mishra", "astro@makaranda.app", "astro123", User.Role.ASTROLOGER,
                "Maithil Brahmin. Kundali, Milan, Muhurta. Darbhanga & Madhubani parampara.", 18, 751);
        user("Acharya Kavita Jha", "kavita@makaranda.app", "astro123", User.Role.ASTROLOGER,
                "Prashna, Stri jataka, and gemstone counsel. Consults in Maithili, Hindi, English.", 12, 501);
        User client = user("Aarav Sharma", "user@makaranda.app", "user123", User.Role.CLIENT,
                null, null, null);

        ClientProfile cp = new ClientProfile();
        cp.setName("Aarav Sharma");
        cp.setGender("Male");
        cp.setEmail(client.getEmail());
        cp.setOwnerUserId(client.getId());
        cp.setAstrologerId(astro.getId());
        cp.setBirthDateTime(LocalDateTime.of(1992, 8, 15, 6, 12));
        cp.setPlace("Darbhanga, Bihar, India");
        cp.setNotes("First consultation: career + vivah muhurta.");
        clients.save(cp);

        Consultation c = new Consultation();
        c.setClientUserId(client.getId());
        c.setAstrologerId(astro.getId());
        c.setClientProfileId(cp.getId());
        c.setSlotStart(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0));
        c.setSlotEnd(LocalDateTime.now().plusDays(2).withHour(10).withMinute(30).withSecond(0).withNano(0));
        c.setMode(Consultation.Mode.VIDEO);
        c.setStatus(Consultation.Status.CONFIRMED);
        c.setAmountInr(751);
        c.setTopic("Career dasha and marriage window");
        c.setMeetUrl("https://meet.jit.si/makaranda-demo");
        c.setPaymentRef("MOCK-SEED");
        bookings.save(c);

        settings.save(new AppSetting("defaultAyanamsa", "\"SURYA_SIDDHANTA_MAKARANDA\""));
        settings.save(new AppSetting("defaultPanchangMode", "\"SIDDHANTIC\""));
        settings.save(new AppSetting("brandName", "\"Makaranda Jyotish\""));

        article("houses", "bhava-chakra", "The Twelve Bhavas",
                "Each bhava is a stage of the kalapurusha. Kendras (1,4,7,10) are pillars; trikonas (1,5,9) are Lakshmi-sthanas; dusthanas (6,8,12) ripen karma through friction. In Mithila paddhati the lagna is read first in the East-Indian diamond, then Navamsa, then Chandra kundali.");
        article("planets", "nava-graha", "The Nine Grahas",
                "Graha means 'seizer' — a force that grabs the mind. Surya is atma, Chandra is manas, Mangala is energy, Budha is buddhi, Guru is dharma, Shukra is rasa, Shani is time, Rahu is the hunger of the future, Ketu is the ash of the past.");
        article("yogas", "pancha-mahapurusha", "Pancha Mahapurusha Yogas",
                "When Mars, Mercury, Jupiter, Venus or Saturn occupies a kendra in own or exaltation sign, a Mahapurusha yoga is born: Ruchaka, Bhadra, Hamsa, Malavya, Sasa. They describe five classical types of greatness.");
        article("panchang", "makaranda-paddhati", "Makaranda Panchang of Mithila",
                "Makaranda is a 15th-century karana grantha rooted in Surya Siddhanta, long used by Maithil panjikars. This platform defaults to Surya Siddhanta (Makaranda) ayanamsa and Siddhantic ganita, with a one-click toggle to Drik (apparent) positions for comparison.");
        article("matching", "ashtakoota", "Ashtakoota Milan",
                "Eight kootas totalling 36 gunas. Nadi (8) and Bhakoot (7) carry the heaviest weight. Guna milan is necessary but not sufficient — Mangal dosha, dasha sandhi, and Navamsa of Venus/Jupiter must be read together, as Maithil families also weigh gotra and village.");
    }

    private User user(String name, String email, String pass, User.Role role, String bio, Integer years, Integer fee) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPasswordHash(encoder.encode(pass));
        u.setRole(role);
        u.setBio(bio);
        u.setExperienceYears(years);
        if (fee != null) u.setConsultationFeeInr(fee);
        return users.save(u);
    }

    private void article(String cat, String slug, String title, String body) {
        LearningArticle a = new LearningArticle();
        a.setCategory(cat);
        a.setSlug(slug);
        a.setTitle(title);
        a.setBody(body);
        articles.save(a);
    }
}
