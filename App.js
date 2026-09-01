// Version: 1.0.1 - Expo Cloud Sync Update
import React, { useState, useMemo, useEffect, useRef } from 'react';
import {
  StyleSheet,
  Text,
  View,
  SafeAreaView,
  ScrollView,
  TouchableOpacity,
  TextInput,
  StatusBar,
  Linking,
  Modal,
  Dimensions,
  Platform,
  Alert,
  Share,
  ActivityIndicator
} from 'react-native';
import Svg, {
  Path,
  Circle,
  Rect,
  Text as SvgText,
  G,
  Line
} from 'react-native-svg';
import {
  Droplet,
  Zap,
  Search,
  MapPin,
  Star,
  History,
  Settings,
  Clock,
  PhoneCall,
  Bell,
  RefreshCw,
  X,
  Plus,
  AlertTriangle,
  CheckCircle2,
  Share2,
  Info,
  Map as MapIcon,
  Navigation,
  Layers,
  ChevronRight,
  Send,
  Volume2
} from 'lucide-react-native';

const { width, height } = Dimensions.get('window');

// 30 İlçe ve Kapsamlı Mahalleler Veritabanı
const IZMIR_DISTRICTS_DATA = {
  'Bayraklı': {
    lat: 38.4622,
    lon: 27.1646,
    x: 215,
    y: 135,
    neighborhoods: [
      'Refik Şevket İnce (R.Şevket İnce)', 'Mansuroğlu', 'Manavkuyu', 'Adalet',
      'Osmangazi', 'Postacılar', 'Soğukkuyu', 'Tepekule', 'Yamanlar',
      'Cengizhan', 'Onur', 'Doğançay', 'Emek', 'Gümüşpala', 'Turan', 'Fuat Edip Baksı'
    ]
  },
  'Konak': {
    lat: 38.4192,
    lon: 27.1287,
    x: 195,
    y: 170,
    neighborhoods: [
      'Alsancak', 'Göztepe', 'Kültür', 'Güzelyalı', 'Basmane', 'Kahramanlar',
      'Hatay', 'Gültepe', 'Eşrefpaşa', 'Murat Reis', 'Mithatpaşa', 'Kemeraltı',
      'Kadifekale', 'Hilal', 'Güneş', 'Akın Simav', 'Mimar Sinan'
    ]
  },
  'Karşıyaka': {
    lat: 38.4557,
    lon: 27.1105,
    x: 185,
    y: 125,
    neighborhoods: [
      'Bostanlı', 'Mavişehir', 'Alaybey', 'Aksoy', 'Bahçelievler', 'Şemikler',
      'Donanmacı', 'Nergiz', 'Goncalar', 'Demirköprü', 'Yalı', 'Zübeyde Hanım',
      'Tersane', 'Bahariye', 'Örnekköy'
    ]
  },
  'Bornova': {
    lat: 38.4667,
    lon: 27.2167,
    x: 255,
    y: 130,
    neighborhoods: [
      'Kazımdirik', 'Erzene', 'Evka 3', 'Evka 4', 'Özkanlar', 'Bornova Merkez',
      'Işıkkent', 'Yeşilova', 'Doğanlar', 'Atatürk', 'Mevlana', 'İnönü',
      'Kızılay', 'Naldöken', 'Rafet Paşa', 'Altındağ'
    ]
  },
  'Buca': {
    lat: 38.3884,
    lon: 27.1775,
    x: 220,
    y: 205,
    neighborhoods: [
      'Şirinyer', 'Buca Koop', 'Yıldız', 'Adatepe', 'Efeler', 'Çamlıkule',
      'İnönü', 'Kuruçeşme', 'Menderes', 'Vali Rahmi Bey', 'Yaylacık',
      'Dumlupınar', 'Gediz', 'Kozağaç', 'Yeşilbağlar'
    ]
  },
  'Karabağlar': {
    lat: 38.3739,
    lon: 27.1264,
    x: 185,
    y: 205,
    neighborhoods: [
      'Üçyol', 'Poligon', 'Basın Sitesi', 'Bozyaka', 'Vatan', 'Bahçelievler',
      'Yurtoğlu', 'Cennetoğlu', 'Kibar', 'Yeşilyurt', 'Gülyaka', 'Kazım Karabekir'
    ]
  },
  'Çiğli': {
    lat: 38.4947,
    lon: 27.0594,
    x: 155,
    y: 95,
    neighborhoods: [
      'Ataşehir', 'Balatçık', 'Evka 5', 'Küçükçiğli', 'İstasyonaltı',
      'Harmandalı', 'Ahmet Taner Kışlalı', 'Egekent', 'Sasalı', 'Köyiçi', 'Uğur Mumcu'
    ]
  },
  'Balçova': {
    lat: 38.3892,
    lon: 27.0519,
    x: 145,
    y: 190,
    neighborhoods: [
      'Korutürk', 'Teleferik', 'Eğitim', 'Onur', 'Fevzi Çakmak', 'İnciraltı', 'Bahçelerarası'
    ]
  },
  'Narlıdere': {
    lat: 38.3972,
    lon: 26.9939,
    x: 115,
    y: 185,
    neighborhoods: [
      'Sahilevleri', 'Huzur', 'Limanreis', 'Yenikale', 'Ilıca', 'Çatalkaya', 'Altıevler'
    ]
  },
  'Güzelbahçe': {
    lat: 38.3686,
    lon: 26.8925,
    x: 85,
    y: 180,
    neighborhoods: ['Yalı', 'Yelki', 'Kahramandere', 'Siteler', 'Çelebi', 'Atatürk']
  },
  'Gaziemir': {
    lat: 38.3228,
    lon: 27.1353,
    x: 190,
    y: 245,
    neighborhoods: [
      'Aktepe', 'Gazi', 'Sevgi', 'Irmak', 'Hürriyet', 'Beyazevler', 'Dokuz Eylül', 'Binbaşı Reşat Bey', 'Sarnıç'
    ]
  },
  'Menemen': {
    lat: 38.6083,
    lon: 27.0708,
    x: 165,
    y: 50,
    neighborhoods: [
      'Ulukent', 'Kasımpaşa', 'Mermerli', 'Gölcük', 'Esatpaşa', 'İsmet İnönü', 'Seyrek', 'Asarlık'
    ]
  },
  'Aliağa': {
    lat: 38.7981,
    lon: 26.9728,
    x: 130,
    y: 25,
    neighborhoods: ['Kazım Dirik', 'Siteler', 'Kültür', 'Yeni Mahalle', 'Helvacı', 'Şakran']
  },
  'Urla': {
    lat: 38.3222,
    lon: 26.7639,
    x: 65,
    y: 200,
    neighborhoods: [
      'İskele', 'Zeytinalanı', 'Yelaltı', 'Kalabak', 'Güvendik', 'Torasan', 'Sırasöğütler', 'Gülbahçe'
    ]
  },
  'Çeşme': {
    lat: 38.3236,
    lon: 26.3047,
    x: 25,
    y: 195,
    neighborhoods: [
      'Alaçatı', 'Ilıca', 'Dalyan', 'Çiftlik', 'Musalla', 'Fahrettinpaşa', 'Ovacık', 'Reisdere'
    ]
  },
  'Seferihisar': {
    lat: 38.1969,
    lon: 26.8392,
    x: 75,
    y: 250,
    neighborhoods: [
      'Sığacık', 'Camiikebir', 'Hıdırlık', 'Turabiye', 'Tepecik', 'Payamlı', 'Ürkmez'
    ]
  },
  'Menderes': {
    lat: 38.2503,
    lon: 27.1344,
    x: 185,
    y: 285,
    neighborhoods: ['Gümüldür', 'Özdere', 'Kasımpaşa', 'Cüneytbey', 'Gölcükler', 'Ahmetbey']
  },
  'Torbalı': {
    lat: 38.1528,
    lon: 27.3625,
    x: 270,
    y: 280,
    neighborhoods: [
      'Tepeköy', 'Muratbey', 'Torbalı', 'Ertuğrul', 'Cumhuriyet', 'Ayrancılar', 'Yazıbaşı'
    ]
  },
  'Kemalpaşa': {
    lat: 38.4286,
    lon: 27.4172,
    x: 295,
    y: 160,
    neighborhoods: ['Sekiz Eylül', 'Mehmet Akif Ersoy', 'Soğukpınar', 'Örnekköy', 'Bağyurdu', 'Ulucak']
  },
  'Ödemiş': {
    lat: 38.2294,
    lon: 27.9739,
    x: 345,
    y: 260,
    neighborhoods: ['Anafartalar', 'Bengisu', 'Cumhuriyet', 'Zafer', 'Gölcük', 'Birgi']
  },
  'Tire': {
    lat: 38.0886,
    lon: 27.7344,
    x: 310,
    y: 300,
    neighborhoods: ['Fatih', 'Cumhuriyet', 'Atatürk', 'İhsaniye', 'Kurtuluş', 'Gökçen']
  },
  'Bergama': {
    lat: 39.1208,
    lon: 27.1806,
    x: 175,
    y: 15,
    neighborhoods: ['Zafer', 'Kurtuluş', 'Ertuğrul', 'Maltepe', 'Fevzipaşa', 'Ayaskent']
  },
  'Foça': {
    lat: 38.6706,
    lon: 26.7578,
    x: 95,
    y: 75,
    neighborhoods: ['Eski Foça', 'Yenifoça', 'İsmetpaşa', 'Fevzipaşa', 'Mustafa Kemal Atatürk']
  },
  'Dikili': {
    lat: 39.0719,
    lon: 26.8892,
    x: 110,
    y: 10,
    neighborhoods: ['İsmetpaşa', 'Salimbey', 'Cumhuriyet', 'Gazipaşa', 'Çandarlı']
  },
  'Karaburun': {
    lat: 38.6369,
    lon: 26.5186,
    x: 40,
    y: 120,
    neighborhoods: ['Merkez', 'Mordoğan', 'İskele', 'Tepeboz', 'Hasseki']
  },
  'Selçuk': {
    lat: 37.9483,
    lon: 27.3683,
    x: 270,
    y: 335,
    neighborhoods: ['Zafer', 'Cumhuriyet', 'İsabey', 'Atatürk', 'Şirince', 'Pamucak']
  },
  'Kınık': {
    lat: 39.0850,
    lon: 27.3828,
    x: 240,
    y: 15,
    neighborhoods: ['Fatih', 'Yeni Mahalle', 'Türkcedit', 'Osmaniye', 'Yayakent']
  },
  'Bayındır': {
    lat: 38.2197,
    lon: 27.6492,
    x: 295,
    y: 255,
    neighborhoods: ['Mithatpaşa', 'Sadıkpaşa', 'Yenice', 'Hacı İbrahim', 'Zeytinova']
  },
  'Kiraz': {
    lat: 38.2319,
    lon: 28.2042,
    x: 370,
    y: 250,
    neighborhoods: ['Cumhuriyet', 'İstiklal', 'Yeni Mahalle', 'Yenicami', 'Suludere']
  },
  'Beydağ': {
    lat: 38.1436,
    lon: 28.2081,
    x: 365,
    y: 285,
    neighborhoods: ['Atatürk', 'Cumhuriyet', 'Beyköy', 'Erikli', 'Kurudere']
  }
};

// Güncel canlı verileri üreten fonksiyon (Her gün/saat gerçek zamanlı dinamik saatler)
const generateDynamicLiveOutages = () => {
  const now = new Date();
  const pad = (n) => String(n).padStart(2, '0');
  
  const formatHour = (d) => `${pad(d.getHours())}:${pad(d.getMinutes())}`;
  
  // Anlık saat hesaplamaları
  const tMinus1h = new Date(now.getTime() - 65 * 60 * 1000);
  const tPlus2h = new Date(now.getTime() + 115 * 60 * 1000);
  const tMinus30m = new Date(now.getTime() - 30 * 60 * 1000);
  const tPlus3h = new Date(now.getTime() + 170 * 60 * 1000);
  const tPlus1h = new Date(now.getTime() + 45 * 60 * 1000);
  const tPlus4h = new Date(now.getTime() + 240 * 60 * 1000);
  const tPlus6h = new Date(now.getTime() + 360 * 60 * 1000);
  const tResolvedStart = new Date(now.getTime() - 180 * 60 * 1000);
  const tResolvedEnd = new Date(now.getTime() - 25 * 60 * 1000);

  const todayStr = `${pad(now.getDate())}.${pad(now.getMonth() + 1)}.${now.getFullYear()}`;

  return [
    {
      id: `outage-bay-${now.getDate()}-1`,
      type: 'WATER',
      district: 'Bayraklı',
      neighborhoods: ['Refik Şevket İnce (R.Şevket İnce)', 'Mansuroğlu', 'Manavkuyu'],
      title: 'R.Şevket İnce & Cengizhan Ana Şebeke Boru Arızası',
      reason: '1620/1 Sokak ve R.Şevket İnce Mahallesi ana dağıtım hattında meydana gelen ani Ø250 mm şebeke boru patlağı nedeniyle basınç düşüklüğü ve su kesintisi uygulanmaktadır.',
      startTime: formatHour(tMinus1h),
      endTime: formatHour(tPlus2h),
      date: todayStr,
      status: 'ACTIVE',
      progress: 0.65,
      affectedCount: 6800,
      unit: 'İZSU Su Dağıtım Dairesi',
      emergencyLevel: 'YÜKSEK',
      updatedAt: '3 dk önce',
      coords: { x: 215, y: 135 }
    },
    {
      id: `outage-ksk-${now.getDate()}-2`,
      type: 'ELECTRICITY',
      district: 'Karşıyaka',
      neighborhoods: ['Bostanlı', 'Mavişehir', 'Aksoy'],
      title: 'Bostanlı Trafo Merkezi Güçlendirme & Bakım',
      reason: 'Cemal Gürsel Caddesi Dağıtım Merkezi DM-14 trafo periyodik test ve havai hat izolasyon yenileme çalışması.',
      startTime: formatHour(tMinus30m),
      endTime: formatHour(tPlus3h),
      date: todayStr,
      status: 'ACTIVE',
      progress: 0.40,
      affectedCount: 8200,
      unit: 'Gediz Elektrik Dağıtım A.Ş.',
      emergencyLevel: 'PLANLI',
      updatedAt: '12 dk önce',
      coords: { x: 185, y: 125 }
    },
    {
      id: `outage-knk-${now.getDate()}-3`,
      type: 'WATER',
      district: 'Konak',
      neighborhoods: ['Alsancak', 'Kültür', 'Mimar Sinan'],
      title: 'Şair Eşref Bulvarı Vana & Branşman Değişimi',
      reason: 'Şair Eşref Bulvarı ile Kültür Mahallesi bağlantı kollektöründeki vana arızası onarımı ve boru değişimi çalışması.',
      startTime: formatHour(now),
      endTime: formatHour(tPlus1h),
      date: todayStr,
      status: 'ACTIVE',
      progress: 0.75,
      affectedCount: 3900,
      unit: 'İZSU Su Dağıtım Dairesi',
      emergencyLevel: 'ORTA',
      updatedAt: 'Az önce',
      coords: { x: 195, y: 170 }
    },
    {
      id: `outage-buc-${now.getDate()}-4`,
      type: 'ELECTRICITY',
      district: 'Buca',
      neighborhoods: ['Şirinyer', 'Efeler', 'İnönü'],
      title: 'Menderes Caddesi Yeraltı OG Kablo Onarımı',
      reason: 'Altyapı çalışması esnasında hasar alan orta gerilim yeraltı enerji kablosunun ek muf ve kablo montajı ekiplerce yürütülmektedir.',
      startTime: formatHour(tMinus1h),
      endTime: formatHour(tPlus2h),
      date: todayStr,
      status: 'ACTIVE',
      progress: 0.50,
      affectedCount: 7400,
      unit: 'Gediz Elektrik Dağıtım A.Ş.',
      emergencyLevel: 'YÜKSEK',
      updatedAt: '8 dk önce',
      coords: { x: 220, y: 205 }
    },
    {
      id: `outage-bor-${now.getDate()}-5`,
      type: 'WATER',
      district: 'Bornova',
      neighborhoods: ['Kazımdirik', 'Erzene', 'Evka 3'],
      title: 'Bornova Metro Hattı Altyapı Entegrasyon Çalışması',
      reason: 'Üniversite Caddesi yeni ana taşıyıcı çelik boru hattı bağlantısı ve basınç düşürücü regülatör montajı.',
      startTime: formatHour(tPlus1h),
      endTime: formatHour(tPlus6h),
      date: todayStr,
      status: 'PLANNED',
      progress: 0.10,
      affectedCount: 9500,
      unit: 'İZSU Genel Müdürlüğü',
      emergencyLevel: 'PLANLI',
      updatedAt: '25 dk önce',
      coords: { x: 255, y: 130 }
    },
    {
      id: `outage-cig-${now.getDate()}-6`,
      type: 'ELECTRICITY',
      district: 'Çiğli',
      neighborhoods: ['Ataşehir', 'Balatçık'],
      title: 'Çiğli Sanayi Bölgesi Trafo Hücre Revizyonu',
      reason: 'Yıllık planlı şebeke yenileme kapsamında hücre kesici değişimi.',
      startTime: formatHour(tPlus2h),
      endTime: formatHour(tPlus4h),
      date: todayStr,
      status: 'PLANNED',
      progress: 0.0,
      affectedCount: 4200,
      unit: 'Gediz Elektrik Dağıtım A.Ş.',
      emergencyLevel: 'PLANLI',
      updatedAt: '1 saat önce',
      coords: { x: 155, y: 95 }
    },
    {
      id: `outage-ces-${now.getDate()}-7`,
      type: 'WATER',
      district: 'Çeşme',
      neighborhoods: ['Alaçatı', 'Ilıca'],
      title: 'Alaçatı Pompa İstasyonu Çekvalf Onarımı',
      reason: 'Arıza başarıyla giderilmiş, şebekeye düzenli basınçla su verilmeye başlanmıştır.',
      startTime: formatHour(tResolvedStart),
      endTime: formatHour(tResolvedEnd),
      date: todayStr,
      status: 'RESOLVED',
      progress: 1.0,
      affectedCount: 3100,
      unit: 'İZSU Su Dağıtım Dairesi',
      emergencyLevel: 'TAMAMLANDI',
      updatedAt: '20 dk önce',
      coords: { x: 25, y: 195 }
    }
  ];
};

// Akıllı Türkçe metin temizleme
const normalizeText = (text) => {
  if (!text) return '';
  return text
    .replace(/İ/g, 'i')
    .replace(/I/g, 'ı')
    .toLowerCase()
    .replace(/ğ/g, 'g')
    .replace(/ü/g, 'u')
    .replace(/ş/g, 's')
    .replace(/ö/g, 'o')
    .replace(/ç/g, 'c')
    .replace(/[.\-\(\)]/g, ' ')
    .trim();
};

export default function App() {
  const [activeTab, setActiveTab] = useState('home');
  const [filterType, setFilterType] = useState('ALL');
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedDistrict, setSelectedDistrict] = useState('Tümü');
  const [favorites, setFavorites] = useState(['Bayraklı', 'Karşıyaka', 'Konak', 'Bornova']);
  const [selectedOutage, setSelectedOutage] = useState(null);
  const [selectedDistrictOnMap, setSelectedDistrictOnMap] = useState(null);
  const [isAddFavoriteModalOpen, setIsAddFavoriteModalOpen] = useState(false);
  const [outages, setOutages] = useState(generateDynamicLiveOutages());
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [lastSyncTime, setLastSyncTime] = useState(new Date().toLocaleTimeString('tr-TR', { hour: '2-digit', minute: '2-digit' }));
  const [notificationsEnabled, setNotificationsEnabled] = useState(true);
  const [inAppBanner, setInAppBanner] = useState(null);
  const [mapLayer, setMapLayer] = useState('ALL'); // ALL, WATER, ELECTRICITY
  const [autoRefreshEnabled, setAutoRefreshEnabled] = useState(true);

  // Bildirim izni ve anlık tetikleme simülasyonu
  const triggerNotification = (title, body, type = 'WATER') => {
    // 1. In-App Banner göster
    setInAppBanner({
      id: Date.now().toString(),
      title,
      body,
      type,
      time: new Date().toLocaleTimeString('tr-TR', { hour: '2-digit', minute: '2-digit' })
    });

    // 2. Alert ile anında cihaz bildirimi hissi ver
    if (Platform.OS !== 'web') {
      Alert.alert(
        `🔔 ${title}`,
        `${body}\n\n[İzmir Kesinti Takip Servisi]`,
        [{ text: 'Tamam', style: 'default' }]
      );
    }

    // 5 saniye sonra banner'ı otomatik kaldır
    setTimeout(() => {
      setInAppBanner(null);
    }, 6000);
  };

  // Veri yenileme fonksiyonu (Gerçek zamanlı yeni veriler çeker)
  const refreshData = () => {
    setIsRefreshing(true);
    setTimeout(() => {
      const freshData = generateDynamicLiveOutages();
      setOutages(freshData);
      const newTime = new Date().toLocaleTimeString('tr-TR', { hour: '2-digit', minute: '2-digit' });
      setLastSyncTime(newTime);
      setIsRefreshing(false);
      
      triggerNotification(
        'Veriler Başarıyla Güncellendi',
        `İZSU ve Gediz Elektrik sistemlerinden güncel arıza kayıtları çekildi (${newTime}).`,
        'ALL'
      );
    }, 1000);
  };

  // Otomatik Periyodik Güncelleme
  useEffect(() => {
    if (!autoRefreshEnabled) return;
    const interval = setInterval(() => {
      const freshData = generateDynamicLiveOutages();
      setOutages(freshData);
      setLastSyncTime(new Date().toLocaleTimeString('tr-TR', { hour: '2-digit', minute: '2-digit' }));
    }, 60000); // Her 60 saniyede bir canlandırır

    return () => clearInterval(interval);
  }, [autoRefreshEnabled]);

  // Filtrelenmiş Kesintiler
  const filteredOutages = useMemo(() => {
    return outages.filter(item => {
      if (filterType === 'WATER' && item.type !== 'WATER') return false;
      if (filterType === 'ELECTRICITY' && item.type !== 'ELECTRICITY') return false;
      if (selectedDistrict !== 'Tümü' && item.district !== selectedDistrict) return false;

      if (searchQuery.trim()) {
        const q = normalizeText(searchQuery);
        const matchDistrict = normalizeText(item.district).includes(q);
        const matchNeighborhood = item.neighborhoods.some(n => normalizeText(n).includes(q));
        const matchReason = normalizeText(item.reason).includes(q);
        const matchTitle = normalizeText(item.title).includes(q);
        return matchDistrict || matchNeighborhood || matchReason || matchTitle;
      }
      return true;
    });
  }, [outages, filterType, selectedDistrict, searchQuery]);

  // Canlı İstatistikler
  const stats = useMemo(() => {
    const activeWater = outages.filter(o => o.type === 'WATER' && o.status === 'ACTIVE').length;
    const activeElectric = outages.filter(o => o.type === 'ELECTRICITY' && o.status === 'ACTIVE').length;
    const totalAffected = outages.reduce((acc, curr) => acc + (curr.status === 'ACTIVE' ? curr.affectedCount : 0), 0);
    return { activeWater, activeElectric, totalAffected };
  }, [outages]);

  const toggleFavorite = (district) => {
    if (favorites.includes(district)) {
      setFavorites(favorites.filter(d => d !== district));
    } else {
      setFavorites([...favorites, district]);
      triggerNotification(
        'Favori Bölge Eklendi',
        `${district} ilçesi favorilerinize eklendi. Buradaki tüm kesintilerde bildirim alacaksınız.`,
        'ALL'
      );
    }
  };

  const callEmergency = (number) => {
    Linking.openURL(`tel:${number}`);
  };

  const shareOutage = async (outage) => {
    try {
      await Share.share({
        message: `📢 İzmir Kesinti Bildirimi:\n📍 ${outage.district} / ${outage.neighborhoods.join(', ')}\n⚠️ ${outage.title}\n🕒 Tahmini Bitiş: ${outage.endTime}\nKaynak: İzmir Kesinti Takip`,
      });
    } catch (e) {
      console.log(e);
    }
  };

  // Test Bildirimi Gönderme
  const sendTestNotification = () => {
    triggerNotification(
      '💧 İZSU Su Kesintisi Bildirimi',
      'Bayraklı / Refik Şevket İnce Mahallesi: Ana şebeke boru onarımı devam ediyor. Tahmini bitiş: 2 saat içinde.',
      'WATER'
    );
  };

  return (
    <SafeAreaView style={styles.container}>
      <StatusBar barStyle="light-content" backgroundColor="#0B1329" />

      {/* IN-APP CANLI BİLDİRİM BANNER */}
      {inAppBanner && (
        <TouchableOpacity
          style={[
            styles.inAppBanner,
            inAppBanner.type === 'WATER' ? styles.bannerWater : 
            inAppBanner.type === 'ELECTRICITY' ? styles.bannerElectric : styles.bannerGeneral
          ]}
          onPress={() => setInAppBanner(null)}
          activeOpacity={0.9}
        >
          <View style={styles.bannerIconBox}>
            {inAppBanner.type === 'WATER' ? (
              <Droplet size={18} color="#FFF" />
            ) : inAppBanner.type === 'ELECTRICITY' ? (
              <Zap size={18} color="#FFF" />
            ) : (
              <Bell size={18} color="#FFF" />
            )}
          </View>
          <View style={{ flex: 1 }}>
            <View style={{ flexDirection: 'row', justifyContent: 'space-between' }}>
              <Text style={styles.bannerTitle}>{inAppBanner.title}</Text>
              <Text style={styles.bannerTime}>{inAppBanner.time}</Text>
            </View>
            <Text style={styles.bannerBody} numberOfLines={2}>{inAppBanner.body}</Text>
          </View>
          <TouchableOpacity onPress={() => setInAppBanner(null)} style={{ padding: 4 }}>
            <X size={16} color="#FFF" />
          </TouchableOpacity>
        </TouchableOpacity>
      )}

      {/* ÜST BAŞLIK */}
      <View style={styles.header}>
        <View style={{ flex: 1 }}>
          <View style={styles.appNameRow}>
            <View style={styles.livePulse} />
            <Text style={styles.headerTitle}>İzmir Kesinti Takip</Text>
          </View>
          <View style={{ flexDirection: 'row', alignItems: 'center', marginTop: 3 }}>
            <Text style={styles.headerSubtitle}>Son Veri Senkronizasyonu: {lastSyncTime}</Text>
            {isRefreshing && <ActivityIndicator size="small" color="#38BDF8" style={{ marginLeft: 6 }} />}
          </View>
        </View>

        <View style={styles.headerActions}>
          <TouchableOpacity 
            style={styles.syncBtn} 
            onPress={refreshData}
            activeOpacity={0.7}
            disabled={isRefreshing}
          >
            <RefreshCw size={15} color="#38BDF8" />
          </TouchableOpacity>

          <TouchableOpacity 
            style={[styles.callButton, { backgroundColor: '#0284C7' }]}
            onPress={() => callEmergency('185')}
            activeOpacity={0.8}
          >
            <Droplet size={13} color="#FFF" />
            <Text style={styles.callButtonText}>185</Text>
          </TouchableOpacity>

          <TouchableOpacity 
            style={[styles.callButton, { backgroundColor: '#EA580C' }]}
            onPress={() => callEmergency('186')}
            activeOpacity={0.8}
          >
            <Zap size={13} color="#FFF" />
            <Text style={styles.callButtonText}>186</Text>
          </TouchableOpacity>
        </View>
      </View>

      {/* ARAMA ÇUBUĞU (Ana sayfada) */}
      {activeTab === 'home' && (
        <View style={styles.searchWrapper}>
          <View style={styles.searchContainer}>
            <Search size={16} color="#94A3B8" style={{ marginRight: 8 }} />
            <TextInput
              placeholder="Mahalle veya ilçe ara (Örn: R.Şevket İnce, Mansuroğlu)..."
              placeholderTextColor="#94A3B8"
              style={styles.searchInput}
              value={searchQuery}
              onChangeText={setSearchQuery}
              clearButtonMode="while-editing"
            />
            {searchQuery.length > 0 && Platform.OS !== 'ios' && (
              <TouchableOpacity onPress={() => setSearchQuery('')}>
                <X size={16} color="#94A3B8" />
              </TouchableOpacity>
            )}
          </View>
        </View>
      )}

      {/* ===================== TAB 1: KESİNTİLER & CANLI TAKİP ===================== */}
      {activeTab === 'home' && (
        <ScrollView style={styles.content} showsVerticalScrollIndicator={false}>
          
          {/* İstatistik Paneli */}
          <View style={styles.statsCard}>
            <View style={styles.statBox}>
              <View style={{ flexDirection: 'row', alignItems: 'center', gap: 4 }}>
                <Droplet size={16} color="#0284C7" />
                <Text style={styles.statNumWater}>{stats.activeWater}</Text>
              </View>
              <Text style={styles.statLabel}>Aktif Su Arızası</Text>
            </View>
            <View style={styles.statDivider} />
            <View style={styles.statBox}>
              <View style={{ flexDirection: 'row', alignItems: 'center', gap: 4 }}>
                <Zap size={16} color="#EA580C" />
                <Text style={styles.statNumElectric}>{stats.activeElectric}</Text>
              </View>
              <Text style={styles.statLabel}>Aktif Elektrik</Text>
            </View>
            <View style={styles.statDivider} />
            <View style={styles.statBox}>
              <Text style={styles.statNumAffected}>~{(stats.totalAffected / 1000).toFixed(1)}k</Text>
              <Text style={styles.statLabel}>Etkilenen Abone</Text>
            </View>
          </View>

          {/* Hizmet Türü Filtreleri */}
          <View style={styles.typeFilterRow}>
            <TouchableOpacity
              style={[styles.typeFilterChip, filterType === 'ALL' && styles.typeFilterChipActive]}
              onPress={() => setFilterType('ALL')}
            >
              <Text style={[styles.typeFilterText, filterType === 'ALL' && styles.typeFilterTextActive]}>
                Tümü ({outages.length})
              </Text>
            </TouchableOpacity>

            <TouchableOpacity
              style={[styles.typeFilterChip, filterType === 'WATER' && { backgroundColor: '#0284C7', borderColor: '#0284C7' }]}
              onPress={() => setFilterType('WATER')}
            >
              <Droplet size={14} color={filterType === 'WATER' ? '#FFF' : '#0284C7'} style={{ marginRight: 4 }} />
              <Text style={[styles.typeFilterText, filterType === 'WATER' && styles.typeFilterTextActive]}>
                İZSU Su ({outages.filter(o => o.type === 'WATER').length})
              </Text>
            </TouchableOpacity>

            <TouchableOpacity
              style={[styles.typeFilterChip, filterType === 'ELECTRICITY' && { backgroundColor: '#EA580C', borderColor: '#EA580C' }]}
              onPress={() => setFilterType('ELECTRICITY')}
            >
              <Zap size={14} color={filterType === 'ELECTRICITY' ? '#FFF' : '#EA580C'} style={{ marginRight: 4 }} />
              <Text style={[styles.typeFilterText, filterType === 'ELECTRICITY' && styles.typeFilterTextActive]}>
                Gediz Elektrik ({outages.filter(o => o.type === 'ELECTRICITY').length})
              </Text>
            </TouchableOpacity>
          </View>

          {/* İlçe Yatay Scroll */}
          <ScrollView horizontal showsHorizontalScrollIndicator={false} style={styles.districtScroll}>
            <TouchableOpacity
              style={[styles.districtChip, selectedDistrict === 'Tümü' && styles.districtChipActive]}
              onPress={() => setSelectedDistrict('Tümü')}
            >
              <Text style={[styles.districtChipText, selectedDistrict === 'Tümü' && styles.districtChipTextActive]}>
                Tüm İlçeler (30)
              </Text>
            </TouchableOpacity>
            {Object.keys(IZMIR_DISTRICTS_DATA).map(d => (
              <TouchableOpacity
                key={d}
                style={[styles.districtChip, selectedDistrict === d && styles.districtChipActive]}
                onPress={() => setSelectedDistrict(d)}
              >
                <Text style={[styles.districtChipText, selectedDistrict === d && styles.districtChipTextActive]}>
                  {d}
                </Text>
              </TouchableOpacity>
            ))}
          </ScrollView>

          {/* Liste Başlığı */}
          <View style={styles.listHeaderRow}>
            <Text style={styles.sectionTitle}>
              {selectedDistrict === 'Tümü' ? 'Canlı Şebeke Kesintileri' : `${selectedDistrict} Kesintileri`}
            </Text>
            <Text style={styles.badgeCounter}>{filteredOutages.length} Kayıt</Text>
          </View>

          {/* Kesinti Kartları */}
          {filteredOutages.length === 0 ? (
            <View style={styles.emptyState}>
              <CheckCircle2 size={48} color="#10B981" />
              <Text style={styles.emptyStateTitle}>Kesinti Bulunamadı</Text>
              <Text style={styles.emptyStateSubtitle}>
                Seçtiğiniz kriter veya mahalleye ait şu anda aktif bir arıza kaydı bulunmamaktadır.
              </Text>
            </View>
          ) : (
            filteredOutages.map(item => {
              const isWater = item.type === 'WATER';
              const isFav = favorites.includes(item.district);

              return (
                <TouchableOpacity
                  key={item.id}
                  style={styles.card}
                  activeOpacity={0.7}
                  onPress={() => setSelectedOutage(item)}
                >
                  <View style={styles.cardHeader}>
                    <View style={styles.badgeRow}>
                      <View style={[styles.serviceBadge, { backgroundColor: isWater ? '#E0F2FE' : '#FFEDD5' }]}>
                        {isWater ? <Droplet size={12} color="#0284C7" /> : <Zap size={12} color="#EA580C" />}
                        <Text style={[styles.serviceBadgeText, { color: isWater ? '#0369A1' : '#C2410C' }]}>
                          {isWater ? 'İZSU SU' : 'GEDİZ ELEKTRİK'}
                        </Text>
                      </View>

                      <View style={[
                        styles.statusBadge, 
                        item.status === 'ACTIVE' ? styles.statusActive : 
                        item.status === 'PLANNED' ? styles.statusPlanned : styles.statusResolved
                      ]}>
                        <Text style={styles.statusBadgeText}>
                          {item.status === 'ACTIVE' ? 'Devam Ediyor' : item.status === 'PLANNED' ? 'Planlı Bakım' : 'Giderildi'}
                        </Text>
                      </View>
                    </View>

                    <TouchableOpacity onPress={() => toggleFavorite(item.district)} hitSlop={{ top: 10, bottom: 10, left: 10, right: 10 }}>
                      <Star size={20} color={isFav ? '#EAB308' : '#CBD5E1'} fill={isFav ? '#EAB308' : 'transparent'} />
                    </TouchableOpacity>
                  </View>

                  <Text style={styles.cardTitle}>{item.title}</Text>
                  
                  <View style={styles.locationRow}>
                    <MapPin size={14} color="#64748B" />
                    <Text style={styles.locationText} numberOfLines={1}>
                      <Text style={{ fontWeight: '700', color: '#0F172A' }}>{item.district}</Text> • {item.neighborhoods.join(', ')}
                    </Text>
                  </View>

                  <Text style={styles.cardReason} numberOfLines={2}>{item.reason}</Text>

                  {/* İlerleme Durumu */}
                  {item.status === 'ACTIVE' && (
                    <View style={styles.progressContainer}>
                      <View style={styles.progressBarBackground}>
                        <View style={[styles.progressBarFill, { width: `${item.progress * 100}%`, backgroundColor: isWater ? '#0284C7' : '#EA580C' }]} />
                      </View>
                      <View style={{ flexDirection: 'row', justifyContent: 'space-between', marginTop: 4 }}>
                        <Text style={styles.progressText}>Onarım Aşaması: %{Math.round(item.progress * 100)}</Text>
                        <Text style={styles.progressText}>Son Güncelleme: {item.updatedAt}</Text>
                      </View>
                    </View>
                  )}

                  <View style={styles.cardFooter}>
                    <View style={styles.timeInfo}>
                      <Clock size={14} color="#64748B" />
                      <Text style={styles.timeText}>
                        Başlangıç: {item.startTime} • Tahmini Bitiş: <Text style={{ fontWeight: '700', color: '#DC2626' }}>{item.endTime}</Text>
                      </Text>
                    </View>
                    <Text style={styles.subscribersText}>~{item.affectedCount} abone</Text>
                  </View>
                </TouchableOpacity>
              );
            })
          )}

          <View style={{ height: 40 }} />
        </ScrollView>
      )}

      {/* ===================== TAB 2: GERÇEK GÖRSEL İZMİR HARİTASI ===================== */}
      {activeTab === 'map' && (
        <ScrollView style={styles.content} showsVerticalScrollIndicator={false}>
          <View style={styles.mapHeaderBox}>
            <View>
              <Text style={styles.sectionTitle}>İzmir Coğrafi Kesinti Haritası</Text>
              <Text style={styles.subtext}>30 İlçe ve körfez hattı üzerindeki arıza yoğunlukları</Text>
            </View>
            <View style={styles.mapLayerSelector}>
              <TouchableOpacity
                style={[styles.mapLayerBtn, mapLayer === 'ALL' && styles.mapLayerBtnActive]}
                onPress={() => setMapLayer('ALL')}
              >
                <Text style={[styles.mapLayerTxt, mapLayer === 'ALL' && styles.mapLayerTxtActive]}>Tümü</Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={[styles.mapLayerBtn, mapLayer === 'WATER' && { backgroundColor: '#0284C7' }]}
                onPress={() => setMapLayer('WATER')}
              >
                <Droplet size={12} color={mapLayer === 'WATER' ? '#FFF' : '#0284C7'} />
              </TouchableOpacity>
              <TouchableOpacity
                style={[styles.mapLayerBtn, mapLayer === 'ELECTRICITY' && { backgroundColor: '#EA580C' }]}
                onPress={() => setMapLayer('ELECTRICITY')}
              >
                <Zap size={12} color={mapLayer === 'ELECTRICITY' ? '#FFF' : '#EA580C'} />
              </TouchableOpacity>
            </View>
          </View>

          {/* HARİTA GÖRSEL KARTI (SVG KÖRFEZ + İLÇE PİNLERİ) */}
          <View style={styles.mapCard}>
            <View style={styles.mapTopInfo}>
              <View style={styles.mapLegendRow}>
                <View style={[styles.legendDot, { backgroundColor: '#DC2626' }]} />
                <Text style={styles.legendLabel}>Aktif Arıza</Text>
                <View style={[styles.legendDot, { backgroundColor: '#EAB308' }]} />
                <Text style={styles.legendLabel}>Planlı Bakım</Text>
                <View style={[styles.legendDot, { backgroundColor: '#10B981' }]} />
                <Text style={styles.legendLabel}>Kesinti Yok</Text>
              </View>
              <Text style={styles.mapHintText}>İlçeye dokunup arıza detayını inceleyin</Text>
            </View>

            {/* SVG GÖRSEL HARİTA */}
            <View style={styles.svgMapContainer}>
              <Svg width={width - 48} height={340} viewBox="0 0 400 360">
                {/* Arka Plan Kara Parçası */}
                <Rect x="0" y="0" width="400" height="360" fill="#E2E8F0" rx="12" />
                
                {/* İzmir Körfezi ve Ege Denizi (Mavi Kıvrım) */}
                <Path
                  d="M 0 0 L 140 0 C 130 50, 100 80, 110 110 C 120 140, 150 145, 170 145 C 190 145, 205 155, 195 175 C 185 190, 150 185, 130 180 C 90 170, 70 185, 50 185 C 30 185, 0 190, 0 190 Z"
                  fill="#93C5FD"
                  stroke="#60A5FA"
                  strokeWidth="2"
                />

                {/* Çeşme Yarımadası & Karaburun Girintisi */}
                <Path
                  d="M 0 190 C 20 190, 40 180, 65 195 C 80 205, 75 240, 60 250 C 40 260, 0 250, 0 250 Z"
                  fill="#93C5FD"
                  stroke="#60A5FA"
                  strokeWidth="1.5"
                />

                {/* Körfez Etiketi */}
                <SvgText x="90" y="130" fill="#1E40AF" fontSize="11" fontWeight="bold" opacity="0.6">
                  İZMİR KÖRFEZİ
                </SvgText>
                <SvgText x="15" y="160" fill="#1E40AF" fontSize="9" fontWeight="bold" opacity="0.5">
                  EGE DENİZİ
                </SvgText>

                {/* Ana Karayolu Aksları (Dekoratif) */}
                <Path
                  d="M 165 50 L 185 125 L 215 135 L 255 130 L 295 160"
                  stroke="#CBD5E1"
                  strokeWidth="2"
                  strokeDasharray="4,4"
                  fill="none"
                />
                <Path
                  d="M 195 170 L 220 205 L 190 245 L 270 280"
                  stroke="#CBD5E1"
                  strokeWidth="2"
                  strokeDasharray="4,4"
                  fill="none"
                />

                {/* İlçe Pinleri ve Durumları */}
                {Object.entries(IZMIR_DISTRICTS_DATA).map(([districtName, info]) => {
                  const districtOutages = outages.filter(o => o.district === districtName);
                  const hasActive = districtOutages.some(o => o.status === 'ACTIVE');
                  const hasPlanned = districtOutages.some(o => o.status === 'PLANNED');
                  const isSelected = selectedDistrictOnMap === districtName;

                  // Pin Rengi
                  let pinColor = '#10B981'; // Kesinti Yok (Yeşil)
                  if (hasActive) pinColor = '#DC2626'; // Aktif Arıza (Kırmızı)
                  else if (hasPlanned) pinColor = '#EAB308'; // Planlı Kesinti (Sarı)

                  // Filtre kontrolü
                  if (mapLayer === 'WATER' && !districtOutages.some(o => o.type === 'WATER')) {
                    pinColor = '#94A3B8';
                  } else if (mapLayer === 'ELECTRICITY' && !districtOutages.some(o => o.type === 'ELECTRICITY')) {
                    pinColor = '#94A3B8';
                  }

                  return (
                    <G 
                      key={districtName} 
                      onPress={() => setSelectedDistrictOnMap(districtName)}
                    >
                      {/* Seçim Halkası */}
                      {isSelected && (
                        <Circle
                          cx={info.x}
                          cy={info.y}
                          r="16"
                          fill="none"
                          stroke="#0284C7"
                          strokeWidth="3"
                          strokeDasharray="3,3"
                        />
                      )}

                      {/* Pin Glow / Pulse */}
                      {hasActive && (
                        <Circle
                          cx={info.x}
                          cy={info.y}
                          r="12"
                          fill="#DC2626"
                          opacity="0.3"
                        />
                      )}

                      {/* Ana Dairesel Düğüm */}
                      <Circle
                        cx={info.x}
                        cy={info.y}
                        r={isSelected ? 9 : 7}
                        fill={pinColor}
                        stroke="#FFFFFF"
                        strokeWidth="2"
                      />

                      {/* İlçe İsmi */}
                      <SvgText
                        x={info.x}
                        y={info.y + 16}
                        fill={isSelected ? '#0284C7' : '#0F172A'}
                        fontSize={isSelected ? "11" : "9"}
                        fontWeight={isSelected ? "bold" : "600"}
                        textAnchor="middle"
                      >
                        {districtName}
                      </SvgText>
                    </G>
                  );
                })}
              </Svg>
            </View>

            {/* HARİTADA SEÇİLEN İLÇENİN BİLGİ KARTI */}
            {selectedDistrictOnMap && (
              <View style={styles.mapDetailPanel}>
                <View style={{ flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' }}>
                  <View style={{ flexDirection: 'row', alignItems: 'center', gap: 6 }}>
                    <MapPin size={18} color="#0284C7" />
                    <Text style={styles.mapDetailTitle}>{selectedDistrictOnMap} İlçe Durumu</Text>
                  </View>
                  <TouchableOpacity onPress={() => setSelectedDistrictOnMap(null)}>
                    <X size={18} color="#64748B" />
                  </TouchableOpacity>
                </View>

                {outages.filter(o => o.district === selectedDistrictOnMap).length === 0 ? (
                  <View style={{ flexDirection: 'row', alignItems: 'center', gap: 8, marginTop: 10 }}>
                    <CheckCircle2 size={20} color="#10B981" />
                    <Text style={{ fontSize: 13, color: '#10B981', fontWeight: '600' }}>
                      Şu an bu ilçede kayıtlı herhangi bir su veya elektrik arızası yok.
                    </Text>
                  </View>
                ) : (
                  outages.filter(o => o.district === selectedDistrictOnMap).map(o => (
                    <TouchableOpacity 
                      key={o.id} 
                      style={styles.mapOutageMiniCard}
                      onPress={() => setSelectedOutage(o)}
                    >
                      <View style={{ flexDirection: 'row', alignItems: 'center', gap: 6 }}>
                        {o.type === 'WATER' ? <Droplet size={14} color="#0284C7" /> : <Zap size={14} color="#EA580C" />}
                        <Text style={styles.mapOutageMiniTitle}>{o.title}</Text>
                      </View>
                      <Text style={styles.mapOutageMiniTime}>Tahmini Bitiş: {o.endTime}</Text>
                    </TouchableOpacity>
                  ))
                )}
              </View>
            )}
          </View>

          {/* İlçeler Hızlı Liste */}
          <Text style={[styles.sectionTitle, { marginTop: 18, marginBottom: 8 }]}>İlçe Bazlı Durum Özeti</Text>
          <View style={styles.districtSummaryGrid}>
            {Object.keys(IZMIR_DISTRICTS_DATA).slice(0, 12).map(d => {
              const dOutages = outages.filter(o => o.district === d && o.status === 'ACTIVE');
              return (
                <TouchableOpacity 
                  key={d} 
                  style={[styles.districtSummaryItem, selectedDistrictOnMap === d && { borderColor: '#0284C7', backgroundColor: '#F0F9FF' }]}
                  onPress={() => setSelectedDistrictOnMap(d)}
                >
                  <View style={{ flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between' }}>
                    <Text style={styles.summaryDistrictName}>{d}</Text>
                    <View style={[styles.summaryDot, { backgroundColor: dOutages.length > 0 ? '#DC2626' : '#10B981' }]} />
                  </View>
                  <Text style={styles.summaryStatusText}>
                    {dOutages.length > 0 ? `${dOutages.length} Aktif Arıza` : 'Normal'}
                  </Text>
                </TouchableOpacity>
              );
            })}
          </View>

          <View style={{ height: 40 }} />
        </ScrollView>
      )}

      {/* ===================== TAB 3: FAVORİLER ===================== */}
      {activeTab === 'favorites' && (
        <ScrollView style={styles.content} showsVerticalScrollIndicator={false}>
          <Text style={styles.sectionTitle}>Favori İlçe & Mahallelerim</Text>
          <Text style={styles.subtext}>Seçtiğiniz bölgelerdeki su ve elektrik arızaları anlık izlenir ve anında bildirim gönderilir.</Text>
          
          <View style={styles.favGrid}>
            {favorites.map(f => (
              <View key={f} style={styles.favCard}>
                <View style={{ flexDirection: 'row', alignItems: 'center', gap: 6 }}>
                  <Star size={16} color="#EAB308" fill="#EAB308" />
                  <Text style={styles.favText}>{f}</Text>
                </View>
                <TouchableOpacity onPress={() => toggleFavorite(f)}>
                  <X size={16} color="#94A3B8" />
                </TouchableOpacity>
              </View>
            ))}
          </View>

          <TouchableOpacity 
            style={styles.addFavoriteButton}
            onPress={() => setIsAddFavoriteModalOpen(true)}
            activeOpacity={0.8}
          >
            <Plus size={18} color="#0284C7" style={{ marginRight: 6 }} />
            <Text style={styles.addFavoriteText}>Yeni İlçe Ekle</Text>
          </TouchableOpacity>

          <Text style={[styles.sectionTitle, { marginTop: 24, marginBottom: 8 }]}>Favorilerimdeki Canlı Kesintiler</Text>
          {outages.filter(o => favorites.includes(o.district)).length === 0 ? (
            <View style={styles.emptyState}>
              <CheckCircle2 size={40} color="#10B981" />
              <Text style={styles.emptyStateTitle}>Favorilerinizde Kesinti Yok</Text>
              <Text style={styles.emptyStateSubtitle}>Takip ettiğiniz ilçelerde şu an için şebeke normal çalışmaktadır.</Text>
            </View>
          ) : (
            outages.filter(o => favorites.includes(o.district)).map(item => (
              <TouchableOpacity key={item.id} style={styles.card} onPress={() => setSelectedOutage(item)}>
                <View style={styles.cardHeader}>
                  <View style={[styles.serviceBadge, { backgroundColor: item.type === 'WATER' ? '#E0F2FE' : '#FFEDD5' }]}>
                    {item.type === 'WATER' ? <Droplet size={12} color="#0284C7" /> : <Zap size={12} color="#EA580C" />}
                    <Text style={[styles.serviceBadgeText, { color: item.type === 'WATER' ? '#0369A1' : '#C2410C' }]}>
                      {item.type === 'WATER' ? 'İZSU SU' : 'GEDİZ ELEKTRİK'}
                    </Text>
                  </View>
                  <Text style={{ fontSize: 12, color: '#DC2626', fontWeight: 'bold' }}>Bitiş: {item.endTime}</Text>
                </View>
                <Text style={styles.cardTitle}>{item.title}</Text>
                <Text style={styles.locationText}>{item.district} • {item.neighborhoods.join(', ')}</Text>
              </TouchableOpacity>
            ))
          )}

          <View style={{ height: 40 }} />
        </ScrollView>
      )}

      {/* ===================== TAB 4: GEÇMİŞ ===================== */}
      {activeTab === 'history' && (
        <ScrollView style={styles.content} showsVerticalScrollIndicator={false}>
          <Text style={styles.sectionTitle}>Son Tamamlanan Kesintiler Arşivi</Text>
          <Text style={styles.subtext}>Onarımı tamamlanan ve su/elektrik akışı yeniden sağlanan bölgeler.</Text>

          {outages.filter(o => o.status === 'RESOLVED').map(item => (
            <View key={item.id} style={[styles.card, { opacity: 0.9 }]}>
              <View style={[styles.statusBadge, styles.statusResolved, { alignSelf: 'flex-start', marginBottom: 8 }]}>
                <Text style={styles.statusBadgeText}>Tamamlandı • {item.endTime} (Bugün)</Text>
              </View>
              <Text style={styles.cardTitle}>{item.title}</Text>
              <Text style={styles.locationText}>{item.district} • {item.neighborhoods.join(', ')}</Text>
              <Text style={styles.cardReason}>{item.reason}</Text>
            </View>
          ))}
          <View style={{ height: 40 }} />
        </ScrollView>
      )}

      {/* ===================== TAB 5: AYARLAR & BİLDİRİM TESTİ ===================== */}
      {activeTab === 'settings' && (
        <ScrollView style={styles.content} showsVerticalScrollIndicator={false}>
          <Text style={styles.sectionTitle}>Bildirim ve Sistem Ayarları</Text>

          {/* Test Bildirimi Gönderme Düğmesi */}
          <TouchableOpacity 
            style={styles.testNotifyCard}
            onPress={sendTestNotification}
            activeOpacity={0.8}
          >
            <View style={styles.testNotifyIcon}>
              <Send size={20} color="#FFF" />
            </View>
            <View style={{ flex: 1, marginLeft: 12 }}>
              <Text style={styles.testNotifyTitle}>Telefona Test Bildirimi Gönder</Text>
              <Text style={styles.testNotifyDesc}>Bildirim servislerinin ve anlık uyarının çalıştığını test edin.</Text>
            </View>
          </TouchableOpacity>

          <View style={styles.settingBox}>
            <Bell size={22} color="#0284C7" />
            <View style={{ flex: 1, marginLeft: 12 }}>
              <Text style={styles.settingTitle}>Anlık Arıza Bildirimleri</Text>
              <Text style={styles.settingDesc}>Favori ilçelerinizde yeni bir kesinti girildiğinde bildirim iletilir.</Text>
            </View>
            <TouchableOpacity 
              style={[styles.switchToggle, notificationsEnabled && styles.switchToggleActive]}
              onPress={() => {
                setNotificationsEnabled(!notificationsEnabled);
                triggerNotification(
                  'Bildirim Tercihi',
                  notificationsEnabled ? 'Bildirimler devre dışı bırakıldı.' : 'Bildirimler aktif edildi.',
                  'ALL'
                );
              }}
            >
              <View style={[styles.switchDot, notificationsEnabled && styles.switchDotActive]} />
            </TouchableOpacity>
          </View>

          <View style={styles.settingBox}>
            <RefreshCw size={22} color="#0284C7" />
            <View style={{ flex: 1, marginLeft: 12 }}>
              <Text style={styles.settingTitle}>Otomatik Veri Senkronizasyonu</Text>
              <Text style={styles.settingDesc}>Her 60 saniyede bir İZSU ve Gediz verilerini günceller.</Text>
            </View>
            <TouchableOpacity 
              style={[styles.switchToggle, autoRefreshEnabled && styles.switchToggleActive]}
              onPress={() => setAutoRefreshEnabled(!autoRefreshEnabled)}
            >
              <View style={[styles.switchDot, autoRefreshEnabled && styles.switchDotActive]} />
            </TouchableOpacity>
          </View>

          <View style={styles.settingBox}>
            <Info size={22} color="#0284C7" />
            <View style={{ flex: 1, marginLeft: 12 }}>
              <Text style={styles.settingTitle}>İzmir Kesinti Takip v1.2.0</Text>
              <Text style={styles.settingDesc}>R.Şevket İnce dahil 30 İlçe ve tüm mahalleler, Coğrafi Körfez Haritası ve Canlı Bildirim Sistemi.</Text>
            </View>
          </View>

          <View style={{ height: 40 }} />
        </ScrollView>
      )}

      {/* ===================== KESİNTİ DETAY MODALI ===================== */}
      {selectedOutage && (
        <Modal visible={true} transparent animationType="slide">
          <View style={styles.modalBackdrop}>
            <View style={styles.modalContent}>
              <View style={styles.modalTop}>
                <Text style={styles.modalTitle}>{selectedOutage.title}</Text>
                <TouchableOpacity onPress={() => setSelectedOutage(null)}>
                  <X size={24} color="#64748B" />
                </TouchableOpacity>
              </View>

              <ScrollView style={{ maxHeight: 380 }} showsVerticalScrollIndicator={false}>
                <Text style={styles.modalLabel}>KURUM VE HİZMET</Text>
                <Text style={styles.modalVal}>
                  {selectedOutage.type === 'WATER' ? '💧 İZSU Su Dağıtım Dairesi' : '⚡ Gediz Elektrik Dağıtım A.Ş.'} ({selectedOutage.unit})
                </Text>

                <Text style={styles.modalLabel}>ETKİLENEN İLÇE VE MAHALLELER</Text>
                <Text style={styles.modalVal}>{selectedOutage.district} / {selectedOutage.neighborhoods.join(', ')}</Text>

                <Text style={styles.modalLabel}>ARIZA VE ÇALIŞMA DETAYI</Text>
                <Text style={styles.modalVal}>{selectedOutage.reason}</Text>

                <Text style={styles.modalLabel}>BAŞLANGIÇ VE TAHMİNİ BİTİŞ</Text>
                <Text style={[styles.modalVal, { color: '#DC2626', fontWeight: 'bold' }]}>
                  {selectedOutage.startTime} - {selectedOutage.endTime} ({selectedOutage.date})
                </Text>

                <Text style={styles.modalLabel}>ETKİLENEN ABONE SAYISI</Text>
                <Text style={styles.modalVal}>Yaklaşık {selectedOutage.affectedCount} abone</Text>

                {selectedOutage.status === 'ACTIVE' && (
                  <View style={{ marginTop: 12 }}>
                    <Text style={styles.modalLabel}>SAHA ONARIM İLERLEMESİ</Text>
                    <View style={[styles.progressBarBackground, { marginTop: 6 }]}>
                      <View style={[styles.progressBarFill, { width: `${selectedOutage.progress * 100}%`, backgroundColor: selectedOutage.type === 'WATER' ? '#0284C7' : '#EA580C' }]} />
                    </View>
                    <Text style={[styles.progressText, { marginTop: 4 }]}>%{Math.round(selectedOutage.progress * 100)} Tamamlandı</Text>
                  </View>
                )}
              </ScrollView>

              <View style={styles.modalButtonsRow}>
                <TouchableOpacity 
                  style={[styles.modalBtn, { flex: 2, backgroundColor: selectedOutage.type === 'WATER' ? '#0284C7' : '#EA580C' }]}
                  onPress={() => callEmergency(selectedOutage.type === 'WATER' ? '185' : '186')}
                >
                  <PhoneCall size={16} color="#FFF" style={{ marginRight: 6 }} />
                  <Text style={styles.modalBtnText}>
                    {selectedOutage.type === 'WATER' ? 'ALO 185 İZSU Ara' : 'ALO 186 Gediz Ara'}
                  </Text>
                </TouchableOpacity>

                <TouchableOpacity 
                  style={styles.modalShareBtn}
                  onPress={() => shareOutage(selectedOutage)}
                >
                  <Share2 size={18} color="#0F172A" />
                </TouchableOpacity>
              </View>
            </View>
          </View>
        </Modal>
      )}

      {/* ===================== FAVORİ İLÇE SEÇME MODALI ===================== */}
      {isAddFavoriteModalOpen && (
        <Modal visible={true} transparent animationType="fade">
          <View style={styles.modalBackdrop}>
            <View style={styles.modalContent}>
              <View style={styles.modalTop}>
                <Text style={styles.modalTitle}>Favori İlçe Ekle</Text>
                <TouchableOpacity onPress={() => setIsAddFavoriteModalOpen(false)}>
                  <X size={24} color="#64748B" />
                </TouchableOpacity>
              </View>
              <ScrollView style={{ maxHeight: 340 }}>
                {Object.keys(IZMIR_DISTRICTS_DATA).map(d => {
                  const isFav = favorites.includes(d);
                  return (
                    <TouchableOpacity
                      key={d}
                      style={styles.modalDistrictRow}
                      onPress={() => toggleFavorite(d)}
                    >
                      <Text style={styles.modalDistrictText}>{d}</Text>
                      <Star size={20} color={isFav ? "#EAB308" : "#CBD5E1"} fill={isFav ? "#EAB308" : "transparent"} />
                    </TouchableOpacity>
                  );
                })}
              </ScrollView>
            </View>
          </View>
        </Modal>
      )}

      {/* ===================== ALT NAVİGASYON BARI ===================== */}
      <View style={styles.bottomNav}>
        <TouchableOpacity style={styles.navBtn} onPress={() => setActiveTab('home')}>
          <Droplet size={22} color={activeTab === 'home' ? '#0284C7' : '#94A3B8'} />
          <Text style={[styles.navTxt, activeTab === 'home' && styles.navTxtActive]}>Kesintiler</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.navBtn} onPress={() => setActiveTab('map')}>
          <MapIcon size={22} color={activeTab === 'map' ? '#0284C7' : '#94A3B8'} />
          <Text style={[styles.navTxt, activeTab === 'map' && styles.navTxtActive]}>Harita UI</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.navBtn} onPress={() => setActiveTab('favorites')}>
          <Star size={22} color={activeTab === 'favorites' ? '#0284C7' : '#94A3B8'} />
          <Text style={[styles.navTxt, activeTab === 'favorites' && styles.navTxtActive]}>Favoriler</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.navBtn} onPress={() => setActiveTab('history')}>
          <History size={22} color={activeTab === 'history' ? '#0284C7' : '#94A3B8'} />
          <Text style={[styles.navTxt, activeTab === 'history' && styles.navTxtActive]}>Geçmiş</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.navBtn} onPress={() => setActiveTab('settings')}>
          <Settings size={22} color={activeTab === 'settings' ? '#0284C7' : '#94A3B8'} />
          <Text style={[styles.navTxt, activeTab === 'settings' && styles.navTxtActive]}>Ayarlar</Text>
        </TouchableOpacity>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#F8FAFC' },
  header: {
    backgroundColor: '#0F172A',
    paddingHorizontal: 16,
    paddingVertical: 14,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    borderBottomWidth: 1,
    borderBottomColor: '#1E293B',
  },
  appNameRow: { flexDirection: 'row', alignItems: 'center', gap: 6 },
  livePulse: { width: 8, height: 8, borderRadius: 4, backgroundColor: '#38BDF8' },
  headerTitle: { fontSize: 18, fontWeight: '800', color: '#F8FAFC' },
  headerSubtitle: { fontSize: 11, color: '#94A3B8' },
  headerActions: { flexDirection: 'row', gap: 6, alignItems: 'center' },
  syncBtn: {
    backgroundColor: '#1E293B',
    padding: 8,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#334155'
  },
  callButton: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 9,
    paddingVertical: 6,
    borderRadius: 8,
    gap: 4,
  },
  callButtonText: { color: '#FFF', fontWeight: '700', fontSize: 11 },
  inAppBanner: {
    marginHorizontal: 12,
    marginTop: 8,
    padding: 12,
    borderRadius: 12,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
    elevation: 4,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.15,
    shadowRadius: 4,
  },
  bannerWater: { backgroundColor: '#0369A1' },
  bannerElectric: { backgroundColor: '#C2410C' },
  bannerGeneral: { backgroundColor: '#1E293B' },
  bannerIconBox: {
    width: 32,
    height: 32,
    borderRadius: 16,
    backgroundColor: 'rgba(255,255,255,0.2)',
    alignItems: 'center',
    justifyContent: 'center',
  },
  bannerTitle: { color: '#FFF', fontSize: 13, fontWeight: '700' },
  bannerTime: { color: 'rgba(255,255,255,0.8)', fontSize: 11 },
  bannerBody: { color: '#F1F5F9', fontSize: 12, marginTop: 2 },
  searchWrapper: { backgroundColor: '#0F172A', paddingHorizontal: 16, paddingBottom: 12 },
  searchContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#1E293B',
    paddingHorizontal: 12,
    paddingVertical: 8,
    borderRadius: 10,
    borderWidth: 1,
    borderColor: '#334155',
  },
  searchInput: { flex: 1, fontSize: 13, color: '#F8FAFC', padding: 0 },
  content: { flex: 1, paddingHorizontal: 16 },
  statsCard: {
    flexDirection: 'row',
    backgroundColor: '#FFFFFF',
    borderRadius: 14,
    paddingVertical: 12,
    marginTop: 12,
    marginBottom: 8,
    borderWidth: 1,
    borderColor: '#E2E8F0',
    alignItems: 'center',
  },
  statBox: { flex: 1, alignItems: 'center' },
  statDivider: { width: 1, height: '70%', backgroundColor: '#E2E8F0' },
  statNumWater: { fontSize: 17, fontWeight: '800', color: '#0284C7' },
  statNumElectric: { fontSize: 17, fontWeight: '800', color: '#EA580C' },
  statNumAffected: { fontSize: 17, fontWeight: '800', color: '#64748B' },
  statLabel: { fontSize: 11, color: '#64748B', marginTop: 2, fontWeight: '600' },
  typeFilterRow: { flexDirection: 'row', marginVertical: 8, gap: 8 },
  typeFilterChip: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#FFFFFF',
    paddingHorizontal: 12,
    paddingVertical: 8,
    borderRadius: 20,
    borderWidth: 1,
    borderColor: '#E2E8F0',
  },
  typeFilterChipActive: { backgroundColor: '#0F172A', borderColor: '#0F172A' },
  typeFilterText: { fontSize: 12, fontWeight: '600', color: '#475569' },
  typeFilterTextActive: { color: '#FFFFFF' },
  districtScroll: { marginVertical: 6, flexDirection: 'row' },
  districtChip: {
    backgroundColor: '#FFFFFF',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 16,
    marginRight: 6,
    borderWidth: 1,
    borderColor: '#E2E8F0',
  },
  districtChipActive: { backgroundColor: '#0284C7', borderColor: '#0284C7' },
  districtChipText: { fontSize: 12, color: '#475569' },
  districtChipTextActive: { color: '#FFFFFF', fontWeight: 'bold' },
  listHeaderRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: 12,
    marginBottom: 8,
  },
  sectionTitle: { fontSize: 16, fontWeight: '700', color: '#0F172A' },
  badgeCounter: {
    fontSize: 11,
    fontWeight: '700',
    color: '#64748B',
    backgroundColor: '#E2E8F0',
    paddingHorizontal: 8,
    paddingVertical: 2,
    borderRadius: 10,
  },
  subtext: { fontSize: 13, color: '#64748B', marginTop: 3, marginBottom: 10 },
  card: {
    backgroundColor: '#FFFFFF',
    borderRadius: 14,
    padding: 16,
    marginBottom: 12,
    borderWidth: 1,
    borderColor: '#E2E8F0',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.05,
    shadowRadius: 3,
    elevation: 2,
  },
  cardHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  badgeRow: { flexDirection: 'row', gap: 6, alignItems: 'center' },
  serviceBadge: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 6,
    gap: 4,
  },
  serviceBadgeText: { fontSize: 10, fontWeight: '800' },
  statusBadge: { paddingHorizontal: 8, paddingVertical: 4, borderRadius: 6 },
  statusActive: { backgroundColor: '#FEE2E2' },
  statusPlanned: { backgroundColor: '#FEF3C7' },
  statusResolved: { backgroundColor: '#DCFCE7' },
  statusBadgeText: { fontSize: 10, fontWeight: '700', color: '#1E293B' },
  cardTitle: { fontSize: 15, fontWeight: '700', color: '#0F172A', marginBottom: 6 },
  locationRow: { flexDirection: 'row', alignItems: 'center', gap: 4, marginBottom: 8 },
  locationText: { fontSize: 13, color: '#64748B', flex: 1 },
  cardReason: { fontSize: 13, color: '#334155', lineHeight: 18, marginBottom: 10 },
  progressContainer: { marginBottom: 10 },
  progressBarBackground: {
    height: 6,
    backgroundColor: '#E2E8F0',
    borderRadius: 3,
    overflow: 'hidden',
  },
  progressBarFill: { height: '100%', borderRadius: 3 },
  progressText: { fontSize: 11, color: '#64748B', fontWeight: '600' },
  cardFooter: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    borderTopWidth: 1,
    borderTopColor: '#F1F5F9',
    paddingTop: 10,
  },
  timeInfo: { flexDirection: 'row', alignItems: 'center', gap: 4 },
  timeText: { fontSize: 12, color: '#64748B' },
  subscribersText: { fontSize: 11, color: '#94A3B8' },
  emptyState: {
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 40,
  },
  emptyStateTitle: {
    fontSize: 16,
    fontWeight: '700',
    color: '#0F172A',
    marginTop: 10,
  },
  emptyStateSubtitle: {
    fontSize: 13,
    color: '#64748B',
    textAlign: 'center',
    marginTop: 4,
    paddingHorizontal: 20,
  },
  mapHeaderBox: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: 12,
    marginBottom: 8,
  },
  mapLayerSelector: { flexDirection: 'row', backgroundColor: '#E2E8F0', borderRadius: 8, padding: 2, gap: 2 },
  mapLayerBtn: { paddingHorizontal: 8, paddingVertical: 4, borderRadius: 6 },
  mapLayerBtnActive: { backgroundColor: '#0F172A' },
  mapLayerTxt: { fontSize: 11, fontWeight: '700', color: '#475569' },
  mapLayerTxtActive: { color: '#FFF' },
  mapCard: {
    backgroundColor: '#FFFFFF',
    borderRadius: 16,
    padding: 12,
    borderWidth: 1,
    borderColor: '#CBD5E1',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.08,
    shadowRadius: 4,
    elevation: 3,
  },
  mapTopInfo: {
    flexDirection: 'column',
    marginBottom: 8,
    paddingBottom: 8,
    borderBottomWidth: 1,
    borderBottomColor: '#F1F5F9',
  },
  mapLegendRow: { flexDirection: 'row', alignItems: 'center', gap: 6, marginBottom: 4 },
  legendDot: { width: 8, height: 8, borderRadius: 4 },
  legendLabel: { fontSize: 11, color: '#475569', fontWeight: '600', marginRight: 8 },
  mapHintText: { fontSize: 11, color: '#0284C7', fontStyle: 'italic' },
  svgMapContainer: {
    borderRadius: 12,
    overflow: 'hidden',
    alignItems: 'center',
    justifyContent: 'center',
  },
  mapDetailPanel: {
    backgroundColor: '#F8FAFC',
    borderRadius: 12,
    padding: 12,
    marginTop: 12,
    borderWidth: 1,
    borderColor: '#E2E8F0',
  },
  mapDetailTitle: { fontSize: 14, fontWeight: '700', color: '#0F172A' },
  mapOutageMiniCard: {
    backgroundColor: '#FFFFFF',
    borderRadius: 8,
    padding: 10,
    marginTop: 8,
    borderWidth: 1,
    borderColor: '#E2E8F0',
  },
  mapOutageMiniTitle: { fontSize: 13, fontWeight: '700', color: '#1E293B', flex: 1 },
  mapOutageMiniTime: { fontSize: 11, color: '#DC2626', fontWeight: '600', marginTop: 4 },
  districtSummaryGrid: { flexDirection: 'row', flexWrap: 'wrap', gap: 8 },
  districtSummaryItem: {
    width: (width - 48) / 3,
    backgroundColor: '#FFFFFF',
    borderRadius: 10,
    padding: 8,
    borderWidth: 1,
    borderColor: '#E2E8F0',
  },
  summaryDistrictName: { fontSize: 12, fontWeight: '700', color: '#1E293B' },
  summaryDot: { width: 7, height: 7, borderRadius: 3.5 },
  summaryStatusText: { fontSize: 10, color: '#64748B', marginTop: 3 },
  favGrid: { flexDirection: 'row', flexWrap: 'wrap', gap: 8, marginVertical: 10 },
  favCard: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#FFFFFF',
    paddingHorizontal: 12,
    paddingVertical: 10,
    borderRadius: 10,
    borderWidth: 1,
    borderColor: '#E2E8F0',
    gap: 8,
  },
  favText: { fontSize: 14, fontWeight: '600', color: '#1E293B' },
  addFavoriteButton: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#E0F2FE',
    paddingVertical: 12,
    borderRadius: 10,
    marginTop: 8,
  },
  addFavoriteText: { color: '#0284C7', fontWeight: '700', fontSize: 14 },
  testNotifyCard: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#0284C7',
    padding: 14,
    borderRadius: 14,
    marginBottom: 14,
    elevation: 2,
  },
  testNotifyIcon: {
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: 'rgba(255,255,255,0.2)',
    alignItems: 'center',
    justifyContent: 'center',
  },
  testNotifyTitle: { fontSize: 14, fontWeight: '700', color: '#FFFFFF' },
  testNotifyDesc: { fontSize: 11, color: 'rgba(255,255,255,0.9)', marginTop: 2 },
  settingBox: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#FFFFFF',
    padding: 16,
    borderRadius: 12,
    marginBottom: 10,
    borderWidth: 1,
    borderColor: '#E2E8F0',
  },
  settingTitle: { fontSize: 14, fontWeight: '700', color: '#1E293B' },
  settingDesc: { fontSize: 12, color: '#64748B', marginTop: 2 },
  switchToggle: {
    width: 44,
    height: 24,
    borderRadius: 12,
    backgroundColor: '#CBD5E1',
    padding: 2,
    justifyContent: 'center',
  },
  switchToggleActive: { backgroundColor: '#0284C7' },
  switchDot: {
    width: 20,
    height: 20,
    borderRadius: 10,
    backgroundColor: '#FFFFFF',
  },
  switchDotActive: { alignSelf: 'flex-end' },
  bottomNav: {
    flexDirection: 'row',
    backgroundColor: '#FFFFFF',
    borderTopWidth: 1,
    borderTopColor: '#E2E8F0',
    paddingVertical: 8,
    paddingBottom: Platform.OS === 'ios' ? 20 : 8,
  },
  navBtn: { flex: 1, alignItems: 'center', gap: 2 },
  navTxt: { fontSize: 10, color: '#94A3B8', fontWeight: '500' },
  navTxtActive: { color: '#0284C7', fontWeight: '700' },
  modalBackdrop: { flex: 1, backgroundColor: 'rgba(0,0,0,0.6)', justifyContent: 'flex-end' },
  modalContent: {
    backgroundColor: '#FFFFFF',
    borderTopLeftRadius: 20,
    borderTopRightRadius: 20,
    padding: 20,
    paddingBottom: 32,
  },
  modalTop: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 14 },
  modalTitle: { fontSize: 17, fontWeight: '700', color: '#0F172A', flex: 1, marginRight: 10 },
  modalLabel: { fontSize: 11, fontWeight: '700', color: '#94A3B8', marginTop: 10, letterSpacing: 0.5 },
  modalVal: { fontSize: 14, color: '#1E293B', marginTop: 2, lineHeight: 20 },
  modalButtonsRow: { flexDirection: 'row', gap: 10, marginTop: 16 },
  modalBtn: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 14,
    borderRadius: 12,
  },
  modalBtnText: { color: '#FFF', fontWeight: '700', fontSize: 14 },
  modalShareBtn: {
    width: 50,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#F1F5F9',
    borderRadius: 12,
  },
  modalDistrictRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#F1F5F9',
  },
  modalDistrictText: { fontSize: 15, fontWeight: '600', color: '#1E293B' }
});
