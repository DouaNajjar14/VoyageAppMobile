# 📱 Application Mobile Voyage - Android

Application mobile complète de réservation de voyages (vols, hôtels, circuits) avec système de paiement sécurisé, développée en Kotlin avec connexion à un backend PostgreSQL.

## 🎯 Fonctionnalités

### ✅ Authentification Complète
- Inscription avec confirmation par email
- Connexion sécurisée
- Mot de passe oublié avec réinitialisation par email
- Gestion de session persistante

### ✅ Gestion du Profil
- Informations personnelles (nom, prénom, email, téléphone)
- Préférences de voyage :
  - Budget maximum
  - Langues préférées
  - Destinations favorites
  - Style de voyage (Luxe, Budget, Aventure, Détente)

### ✅ Recherche Intelligente
- Recherche de vols, hôtels et circuits
- Filtres dynamiques :
  - Par destination
  - Par prix (min/max)
  - Par date
- Tri dynamique :
  - Prix croissant/décroissant
  - Popularité
  - Notation

### ✅ Réservation et Paiement Sécurisé
- Sélection d'offres
- 3 méthodes de paiement :
  - 💳 Carte bancaire (avec validation complète)
  - 💰 PayPal
  - 👛 Portefeuille virtuel
- Confirmation de réservation

### ✅ Historique des Réservations
- Liste complète des réservations
- Détails de chaque réservation
- Gestion des annulations
- Statuts (Confirmé, Annulé, En attente)

### ✅ Système de Notifications
- Rappels de vol
- Check-in
- Offres spéciales
- Recommandations locales
- Indicateur de lecture

## 🏗️ Architecture

### Technologies Utilisées
- **Langage** : Kotlin
- **UI** : Material Design 3, ViewBinding
- **Réseau** : Retrofit 2.9.0, Gson
- **Asynchrone** : Coroutines
- **Images** : Glide
- **Backend** : PostgreSQL via API REST

### Pattern Architecture
- **Repository Pattern** pour la couche de données
- **MVVM-like** avec Fragments et Activities
- **Separation of Concerns** (UI, Repository, Network, Models)

### Structure du Projet
```
app/
├── src/main/
│   ├── java/com/example/voyageproject/
│   │   ├── model/              # Modèles de données
│   │   ├── network/            # Configuration Retrofit et API
│   │   ├── repository/         # Repositories pour accès aux données
│   │   ├── ui/                 # Interface utilisateur
│   │   │   ├── login/          # Authentification
│   │   │   ├── register/       # Inscription
│   │   │   ├── forgot/         # Mot de passe oublié
│   │   │   ├── reset/          # Réinitialisation
│   │   │   ├── main/           # Activité principale
│   │   │   ├── home/           # Accueil
│   │   │   ├── search/         # Recherche
│   │   │   ├── offers/         # Offres
│   │   │   ├── payment/        # Paiement
│   │   │   ├── history/        # Historique
│   │   │   ├── notifications/  # Notifications
│   │   │   └── profile/        # Profil
│   │   └── utils/              # Utilitaires
│   └── res/                    # Ressources (layouts, drawables, etc.)
└── build.gradle.kts            # Configuration Gradle
```

## 🚀 Installation et Configuration

### Prérequis
- Android Studio Arctic Fox ou supérieur
- JDK 11 ou supérieur
- Android SDK 24 (Android 7.0) minimum
- Backend PostgreSQL configuré et démarré

### Étapes d'Installation

1. **Cloner le projet**
```bash
git clone <url-du-repo>
cd VoyageProject
```

2. **Ouvrir dans Android Studio**
- File → Open → Sélectionner le dossier du projet

3. **Configurer l'URL du Backend**
- Ouvrir `app/src/main/java/com/example/voyageproject/network/RetrofitClient.kt`
- Modifier `BASE_URL` avec l'adresse de votre backend :
```kotlin
private const val BASE_URL = "http://VOTRE_IP:8085/"
```

4. **Synchroniser Gradle**
- Cliquer sur "Sync Now" dans la barre de notification

5. **Lancer l'application**
- Connecter un appareil ou démarrer un émulateur
- Cliquer sur Run (▶️)

## 📡 Configuration Backend

### Endpoints Requis

Votre backend doit exposer ces endpoints :

#### Authentification
- `POST /api/client/register` - Inscription
- `POST /api/client/login` - Connexion
- `POST /api/client/forgot-password` - Mot de passe oublié
- `POST /api/client/reset-password` - Réinitialisation
- `GET /api/client/confirm` - Confirmation email

#### Profil
- `GET /api/client/profile` - Obtenir le profil
- `PUT /api/client/profile` - Mettre à jour le profil

#### Recherche
- `GET /api/hotels/search` - Rechercher des hôtels
- `GET /api/flights/search` - Rechercher des vols
- `GET /api/circuits/search` - Rechercher des circuits

#### Offres
- `GET /api/hotels` - Liste des hôtels
- `GET /api/flights` - Liste des vols
- `GET /api/circuits` - Liste des circuits

#### Réservations
- `POST /api/reservation` - Créer une réservation
- `GET /api/reservation/history` - Historique des réservations
- `DELETE /api/reservation/{id}` - Annuler une réservation

#### Notifications
- `GET /api/notifications` - Obtenir les notifications
- `PUT /api/notifications/{id}/read` - Marquer comme lu

Voir `EXEMPLES_API.md` pour les détails complets des endpoints.

## 📚 Documentation

- **FONCTIONNALITES.md** - Liste détaillée des fonctionnalités
- **GUIDE_INTEGRATION.md** - Guide d'intégration pas à pas
- **EXEMPLES_API.md** - Exemples d'API et données de test
- **CHECKLIST_TEST.md** - Checklist complète de tests
- **RESUME_IMPLEMENTATION.md** - Résumé de l'implémentation

## 🧪 Tests

### Lancer les Tests
```bash
./gradlew test
```

### Tests Manuels
Suivre la checklist dans `CHECKLIST_TEST.md`

## 📱 Captures d'Écran

### Navigation
- 🏠 Accueil : Affichage des offres par catégorie
- 🔍 Recherche : Filtres et résultats dynamiques
- 📋 Historique : Liste des réservations
- 🔔 Notifications : Alertes et rappels
- 👤 Profil : Informations et préférences

## 🔐 Sécurité

- ✅ Validation des entrées utilisateur
- ✅ Paiement sécurisé avec validation des cartes
- ✅ Gestion sécurisée des sessions
- ✅ Communication HTTPS avec le backend
- ✅ Permissions Android gérées correctement

## 🛠️ Dépendances

```gradle
// Retrofit pour les appels REST
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")

// Coroutines pour appels asynchrones
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// Material Design
implementation("com.google.android.material:material:1.12.0")

// Glide pour les images
implementation("com.github.bumptech.glide:glide:4.16.0")
```

## 🐛 Dépannage

### Problème : Erreur de connexion au backend
**Solution** : Vérifiez que :
- Le backend est démarré
- L'URL dans `RetrofitClient.kt` est correcte
- Votre appareil/émulateur peut accéder au réseau

### Problème : Les fragments ne s'affichent pas
**Solution** : Vérifiez que `fragmentContainer` existe dans `activity_main.xml`

### Problème : Erreur de compilation
**Solution** : 
- Sync Gradle
- Clean Project
- Rebuild Project

## 📞 Support

Pour toute question ou problème :
1. Consultez la documentation dans les fichiers `.md`
2. Vérifiez les logs Android Studio
3. Consultez les commentaires dans le code

## 👥 Contributeurs

- Développement : Votre équipe
- Backend : PostgreSQL + Spring Boot

## 📄 Licence

Ce projet est développé dans un cadre éducatif.

## 🎉 Remerciements

Merci d'utiliser cette application de voyage !

---

**Version** : 1.0  
**Date** : Janvier 2025  
**Plateforme** : Android 7.0+
