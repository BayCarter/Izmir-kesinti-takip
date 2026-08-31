package com.example.model

data class IzmirDistrict(
    val name: String,
    val neighborhoods: List<String>,
    val centerLat: Double,
    val centerLng: Double,
    val population: String
)

object IzmirLocations {
    val ALL_DISTRICTS: List<IzmirDistrict> = listOf(
        IzmirDistrict(
            name = "Bornova",
            neighborhoods = listOf(
                "Kazımdirik", "Erzene", "Evka 3", "Çamdibi", "Işıklar", 
                "Yeşilova", "Altındağ", "Kızılay", "İnönü", "Atatürk", 
                "Mevlana", "Doğanlar", "Kemalpaşa Mah."
            ),
            centerLat = 38.4697, centerLng = 27.2178, population = "450.000+"
        ),
        IzmirDistrict(
            name = "Karşıyaka",
            neighborhoods = listOf(
                "Bostanlı", "Mavişehir", "Alaybey", "Bahçelievler", "Aksoy", 
                "Donanmacı", "Şemikler", "Nergiz", "Dedebaşı", "Tersane", 
                "Yalı", "Goncalar", "Örnekköy"
            ),
            centerLat = 38.4594, centerLng = 27.1122, population = "350.000+"
        ),
        IzmirDistrict(
            name = "Buca",
            neighborhoods = listOf(
                "Şirinyer", "Efeler", "Kozağaç", "Vali Rahmi Bey", "Buca Koop", 
                "Yıldız", "Yaylacık", "Gediz", "Çamlıkule", "Adatepe", 
                "İzkent", "Barış", "Yenigün"
            ),
            centerLat = 38.3882, centerLng = 27.1764, population = "520.000+"
        ),
        IzmirDistrict(
            name = "Konak",
            neighborhoods = listOf(
                "Alsancak", "Kültür", "Akdeniz", "Göztepe", "Kahramanlar", 
                "Basmane", "Çankaya", "Eşrefpaşa", "Hatay", "Güzelyalı", 
                "Hilal", "Mersinli", "Gültepe"
            ),
            centerLat = 38.4192, centerLng = 27.1287, population = "340.000+"
        ),
        IzmirDistrict(
            name = "Bayraklı",
            neighborhoods = listOf(
                "Mansuroğlu", "Adalet", "Manavkuyu", "Osmangazi", "Postacılar", 
                "Tepekule", "Soğukkuyu", "Çiçek", "Gümüşpala", "Fuat Edip Baksı", 
                "Onur", "Körfez"
            ),
            centerLat = 38.4628, centerLng = 27.1654, population = "315.000+"
        ),
        IzmirDistrict(
            name = "Çiğli",
            neighborhoods = listOf(
                "Ataşehir", "Balatçık", "Evka-5", "Küçükçiğli", "Köyiçi", 
                "Harmandalı", "Egekent", "İzkent", "Maltepe", "Sasalı", 
                "Ahmet Taner Kışlalı", "İnönü"
            ),
            centerLat = 38.4975, centerLng = 27.0603, population = "215.000+"
        ),
        IzmirDistrict(
            name = "Balçova",
            neighborhoods = listOf(
                "Eğitim", "Korutürk", "Onur", "Teleferik", "Çetin Emeç", 
                "Bahçelerarası", "Fevzi Çakmak", "İnciraltı"
            ),
            centerLat = 38.3894, centerLng = 27.0503, population = "80.000+"
        ),
        IzmirDistrict(
            name = "Gaziemir",
            neighborhoods = listOf(
                "Aktepe", "Emrez", "Gazi", "Dokuz Eylül", "Sevgi", 
                "Yeşiltepe", "Binbaşı Reşatbey", "Atıfbey", "Fatih", "Hürriyet"
            ),
            centerLat = 38.3228, centerLng = 27.1328, population = "140.000+"
        ),
        IzmirDistrict(
            name = "Karabağlar",
            neighborhoods = listOf(
                "Basın Sitesi", "Yeşilyurt", "Bozyaka", "Poligon", "Vatan", 
                "Kibar", "Fahrettin Altay", "Aşık Veysel", "Gülyaka", "Ali Fuat Cebesoy"
            ),
            centerLat = 38.3725, centerLng = 27.1233, population = "480.000+"
        ),
        IzmirDistrict(
            name = "Narlıdere",
            neighborhoods = listOf(
                "Huzur", "Ilıca", "Limanreis", "Narlı", "Sahilevleri", 
                "Yenikale", "Çamtepe", "2. İnönü", "Çatalkaya"
            ),
            centerLat = 38.3900, centerLng = 26.9950, population = "65.000+"
        ),
        IzmirDistrict(
            name = "Urla",
            neighborhoods = listOf(
                "İskele", "Kalabak", "Çamlıçay", "Yenikent", "Sırasöğütler", 
                "Gülbahçe", "Zeytinalanı", "Torasan", "Özbek", "Kuşçular"
            ),
            centerLat = 38.3222, centerLng = 26.7644, population = "75.000+"
        ),
        IzmirDistrict(
            name = "Çeşme",
            neighborhoods = listOf(
                "Alaçatı", "Ilıca", "Boyalık", "Dalyan", "Çiftlik", 
                "Reisdere", "Ovacık", "Musalla", "Sakarya", "Şifne"
            ),
            centerLat = 38.3236, centerLng = 26.3056, population = "50.000+"
        ),
        IzmirDistrict(
            name = "Menemen",
            neighborhoods = listOf(
                "Kasımpaşa", "Mermerli", "Uğur Mumcu", "Esatpaşa", "Ulukent", 
                "Koyundere", "Seyrek", "Emiralem", "Asarlık"
            ),
            centerLat = 38.6042, centerLng = 27.0678, population = "200.000+"
        ),
        IzmirDistrict(
            name = "Torbalı",
            neighborhoods = listOf(
                "Tepeköy", "Muratbey", "Torbalı Mah.", "Ertuğrul", "Alpkent", 
                "Ayrancılar", "Yazıbaşı", "Subaşı", "Pancar"
            ),
            centerLat = 38.1517, centerLng = 27.3622, population = "210.000+"
        ),
        IzmirDistrict(
            name = "Aliağa",
            neighborhoods = listOf(
                "Kazım Dirik", "Siteler", "Kültür", "Yeni Mahalle", "Helvacı", 
                "Şakran", "Yalı", "Kurtuluş"
            ),
            centerLat = 38.7989, centerLng = 26.9722, population = "105.000+"
        ),
        IzmirDistrict(
            name = "Seferihisar",
            neighborhoods = listOf(
                "Sığacık", "Camiikebir", "Turabiye", "Hıdırlık", "Ürkmez", 
                "Doğanbey", "Ulamış", "Tepecik"
            ),
            centerLat = 38.1978, centerLng = 26.8394, population = "55.000+"
        ),
        IzmirDistrict(
            name = "Kemalpaşa",
            neighborhoods = listOf(
                "Mehmet Akif Ersoy", "Sekiz Eylül", "Soğukpınar", "Örnekköy", 
                "Ulucak", "Bağyurdu", "Armutlu", "Yukarı Kızılca"
            ),
            centerLat = 38.4264, centerLng = 27.4178, population = "115.000+"
        ),
        IzmirDistrict(
            name = "Menderes",
            neighborhoods = listOf(
                "Cüneytbey", "Gazipaşa", "Kasımpaşa", "Özdere", "Gümüldür", 
                "Ahmetbeyli", "Tekeli", "Değirmendere"
            ),
            centerLat = 38.2500, centerLng = 27.1333, population = "105.000+"
        ),
        IzmirDistrict(
            name = "Foça",
            neighborhoods = listOf(
                "İsmetpaşa", "Fevzipaşa", "Atatürk", "Yenifoça", "Bağarası", 
                "Kozbeyli", "Gerenköy"
            ),
            centerLat = 38.6694, centerLng = 26.7578, population = "35.000+"
        ),
        IzmirDistrict(
            name = "Dikili",
            neighborhoods = listOf(
                "Salimbey", "İsmetpaşa", "Cumhuriyet", "Çandarlı", "Bademli", 
                "Kabakum", "Kıratlı"
            ),
            centerLat = 39.0717, centerLng = 26.8889, population = "45.000+"
        ),
        IzmirDistrict(
            name = "Bergama",
            neighborhoods = listOf(
                "İslam", "Atmaca", "Zafer", "Maltepe", "Kurtuluş", 
                "Turabey", "Gaziosmanpaşa", "Ayaskent"
            ),
            centerLat = 39.1233, centerLng = 27.1814, population = "105.000+"
        ),
        IzmirDistrict(
            name = "Ödemiş",
            neighborhoods = listOf(
                "Akıncılar", "Bengisu", "Cumhuriyet", "Anafartalar", "Zafer", 
                "Birgi", "Gölcük", "Bozdağ", "Kaymakçı"
            ),
            centerLat = 38.2300, centerLng = 27.9733, population = "135.000+"
        ),
        IzmirDistrict(
            name = "Tire",
            neighborhoods = listOf(
                "Atatürk", "Cumhuriyet", "Dörteylül", "Fatih", "Hürriyet", 
                "İbni Melek", "Kahrat", "Gökçen"
            ),
            centerLat = 38.0872, centerLng = 27.7314, population = "87.000+"
        ),
        IzmirDistrict(
            name = "Selçuk",
            neighborhoods = listOf(
                "Atatürk", "Cumhuriyet", "İsabey", "Zafer", "Şirince", 
                "Çamlık", "Belevi", "Gökçealan"
            ),
            centerLat = 37.9467, centerLng = 27.3683, population = "38.000+"
        ),
        IzmirDistrict(
            name = "Karaburun",
            neighborhoods = listOf(
                "Merkez", "İskele", "Mordoğan", "Küçükbahçe", "Hasseki", 
                "Parlak", "Saip", "Tepeboz"
            ),
            centerLat = 38.6367, centerLng = 26.5122, population = "12.000+"
        ),
        IzmirDistrict(
            name = "Güzelbahçe",
            neighborhoods = listOf(
                "Yalı", "Çelebi", "Kahramandere", "Siteler", "Yelki", 
                "Mustafa Kemal Paşa", "Maltepe", "Payamlı"
            ),
            centerLat = 38.3700, centerLng = 26.8900, population = "38.000+"
        ),
        IzmirDistrict(
            name = "Kınık",
            neighborhoods = listOf("Fatih", "Osmaniye", "Türkcedit", "Poyracık", "Yayakent"),
            centerLat = 39.0833, centerLng = 27.3833, population = "28.000+"
        ),
        IzmirDistrict(
            name = "Kiraz",
            neighborhoods = listOf("Cumhuriyet", "İstiklal", "Yeni Mahalle", "Haliller", "Çömlekçi"),
            centerLat = 38.2300, centerLng = 28.2044, population = "43.000+"
        ),
        IzmirDistrict(
            name = "Beydağ",
            neighborhoods = listOf("Atatürk", "Cumhuriyet", "Beyköy", "Adaküre", "Bakırköy"),
            centerLat = 38.0850, centerLng = 28.2100, population = "12.000+"
        )
    )

    fun getDistrict(name: String): IzmirDistrict? {
        return ALL_DISTRICTS.firstOrNull { it.name.equals(name, ignoreCase = true) }
    }

    fun getNeighborhoodsForDistrict(districtName: String): List<String> {
        return getDistrict(districtName)?.neighborhoods ?: emptyList()
    }
}
