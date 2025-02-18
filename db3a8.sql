-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : mar. 18 fév. 2025 à 15:44
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `db3a8`
--

-- --------------------------------------------------------

--
-- Structure de la table `assistantdocumentaire`
--

CREATE TABLE `assistantdocumentaire` (
  `id` int(11) NOT NULL,
  `id_utilisateur` int(11) NOT NULL,
  `id_document` int(11) NOT NULL,
  `type_assistance` varchar(255) NOT NULL,
  `date_demande` varchar(255) DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  `remarque` text DEFAULT NULL,
  `rappel_automatique` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `assistantdocumentaire`
--

INSERT INTO `assistantdocumentaire` (`id`, `id_utilisateur`, `id_document`, `type_assistance`, `date_demande`, `status`, `remarque`, `rappel_automatique`) VALUES
(2, 16, 2, 'Nouveau document', '2025-02-12 15:30:00', 'Rejetée', 'Documents incorrects', 0),
(5, 16, 3, 'qefq', '2025-01-31', 'qrqer', 'qrqer', 0),
(6, 16, 3, 'rzrzr', '2025-02-13', 'rzrz', 'rzer', 0),
(7, 16, 2, 'rzrzr', '2025-02-13', 'rzrz', 'rzer', 0);

-- --------------------------------------------------------

--
-- Structure de la table `declarationrevenus`
--

CREATE TABLE `declarationrevenus` (
  `id` int(11) NOT NULL,
  `id_dossier` int(11) NOT NULL,
  `montant_revenu` double NOT NULL,
  `source_revenu` varchar(255) NOT NULL,
  `date_declaration` varchar(255) NOT NULL,
  `preuve_revenu` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `declarationrevenus`
--

INSERT INTO `declarationrevenus` (`id`, `id_dossier`, `montant_revenu`, `source_revenu`, `date_declaration`, `preuve_revenu`) VALUES
(1, 1, 500000, 'saraya', '2025-06-04', '/fichier');

-- --------------------------------------------------------

--
-- Structure de la table `documentadministratif`
--

CREATE TABLE `documentadministratif` (
  `id` int(11) NOT NULL,
  `nomDocument` varchar(255) NOT NULL,
  `cheminFichier` varchar(255) NOT NULL,
  `dateEmission` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL,
  `remarque` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `documentadministratif`
--

INSERT INTO `documentadministratif` (`id`, `nomDocument`, `cheminFichier`, `dateEmission`, `status`, `remarque`) VALUES
(2, 'Permis de travail', '/projet/docs', '2025-02-10', 'Approuvé', 'Vérification en cours'),
(3, 'madhmoun', 'C:\\Users\\ichaa\\Downloads\\UML-.pdf', '2005-03-06', 'en cours', 'pas de remarque'),
(4, 'chhada', 'C:\\Users\\ichaa\\Downloads\\correction examen SR-2024.pdf', '2024-06-04', 'validé', 'remarque'),
(5, 'azefzeaf', 'C:\\Users\\ichaa\\Downloads\\Examen ESE-Rattrapage juin 2024-VF.pdf', '2025-20-03', 'aezfaze', ''),
(6, 'Permis de travail', '/projet/docs', '2025-02-10', 'Approuvé', 'Vérification en cours'),
(7, 'aefz', 'C:\\Users\\ichaa\\Downloads\\Examen ESE-Rattrapage juin 2024-VF.pdf', '2025-01-28', 'fazef', 'aezf'),
(8, 'rar', 'C:\\Users\\ichaa\\Downloads\\resumeFinal-switched-network.pdf', '2025-01-30', 'eaea', ''),
(9, 'rzrz', 'C:\\Users\\ichaa\\Downloads\\correction examen SR-2024.pdf', '2025-01-18', 'eae', '');

-- --------------------------------------------------------

--
-- Structure de la table `dossierfiscale`
--

CREATE TABLE `dossierfiscale` (
  `id` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `annee_fiscale` int(11) NOT NULL,
  `total_impot` double NOT NULL,
  `total_impot_paye` double NOT NULL,
  `status` varchar(255) NOT NULL,
  `date_creation` varchar(255) NOT NULL,
  `moyen_paiement` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `dossierfiscale`
--

INSERT INTO `dossierfiscale` (`id`, `id_user`, `annee_fiscale`, `total_impot`, `total_impot_paye`, `status`, `date_creation`, `moyen_paiement`) VALUES
(1, 16, 2025, 1000, 600, 'En cours', '2025-02-05', 'Carte bancaire'),
(2, 15, 2025, 100000, 10, 'En cours', '2025-02-05', 'cash');

-- --------------------------------------------------------

--
-- Structure de la table `incident`
--

CREATE TABLE `incident` (
  `id` int(11) NOT NULL,
  `type_incident` enum('voirie','éclairage','déchet','autre') NOT NULL,
  `description` text NOT NULL,
  `localisation` varchar(255) NOT NULL,
  `statut` enum('En attente','En cours','Résolu') DEFAULT 'En attente',
  `date_signalement` timestamp NOT NULL DEFAULT current_timestamp(),
  `service_affecte` int(11) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `incident`
--

INSERT INTO `incident` (`id`, `type_incident`, `description`, `localisation`, `statut`, `date_signalement`, `service_affecte`, `latitude`, `longitude`) VALUES
(39, 'déchet', 'aaaaaaa', 'Tatabánya, 2800 Hongrie', 'En attente', '2025-02-11 09:39:18', 2, 0, 0),
(40, 'déchet', 'aaaaaaa', 'Tatabánya, 2800 Hongrie', 'En attente', '2025-02-11 09:39:20', 2, 0, 0),
(42, 'déchet', 'EZRFEZREZ', 'Fès, Maroc', 'Résolu', '2025-02-11 09:39:23', 2, 0, 0),
(43, 'éclairage', 'EFEQMKSFKS', 'R58W+VP Tunis, Tunisia', 'En attente', '2025-02-11 09:53:16', 2, 36.817219802179316, 10.196777862548823),
(44, 'voirie', 'bfffgg', 'Nour Jaafer, Cebalat Ben Ammar, Tunisie', 'En attente', '2025-02-11 10:44:27', 2, 0, 0),
(45, 'éclairage', 'rztze', 'R548+QMG, Rue Sidi El Aloui, Tunis, Tunisia', 'En attente', '2025-02-12 08:57:28', 2, 36.80718721183224, 10.166565460205073),
(46, 'éclairage', 'FFFFFFFDSFDF', 'R554+2J3, RueFrancois, Tunis, Tunisia', 'En cours', '2025-02-12 10:32:16', 2, 36.80842417758757, 10.156609100341791);

-- --------------------------------------------------------

--
-- Structure de la table `serviceintervention`
--

CREATE TABLE `serviceintervention` (
  `id` int(11) NOT NULL,
  `nom_service` varchar(255) NOT NULL,
  `type_intervention` enum('voirie','éclairage','propreté','autre') NOT NULL,
  `zone_intervention` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `serviceintervention`
--

INSERT INTO `serviceintervention` (`id`, `nom_service`, `type_intervention`, `zone_intervention`) VALUES
(2, 'CSDSD', '', 'SDDD'),
(4, 'VSDS', 'voirie', 'VSSSSSSSSSSS'),
(21, 'EYGGRERRG', 'éclairage', 'ZFFZ'),
(22, 'VSDS', 'voirie', 'VSSSSSSSSSSS');

-- --------------------------------------------------------

--
-- Structure de la table `utilisateur`
--

CREATE TABLE `utilisateur` (
  `id` int(11) NOT NULL,
  `nom` varchar(255) NOT NULL,
  `prenom` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `role` enum('Admin','Citoyen','Employé') NOT NULL,
  `dateInscription` date NOT NULL,
  `motDePasse` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `utilisateur`
--

INSERT INTO `utilisateur` (`id`, `nom`, `prenom`, `email`, `role`, `dateInscription`, `motDePasse`) VALUES
(15, 'ismail', 'chaabane', 'ichaabane6@gmail.com', 'Admin', '2025-02-14', '$2a$10$WqfU6gE5HbR1FjkorhXo2Ogh74cGeH1q3cEeJsLKLm2aM2sQOqMe.'),
(16, 'ismail ', 'chaabane', 'ichaabane66@gmail.com', 'Admin', '2025-02-14', '$2a$10$i17qQcuEfs66BfaWHfHdzOTa2nU9q52t9lXMqSb3kz1EA9sCqyUFa');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `assistantdocumentaire`
--
ALTER TABLE `assistantdocumentaire`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_utilisateur` (`id_utilisateur`),
  ADD KEY `id_document` (`id_document`);

--
-- Index pour la table `declarationrevenus`
--
ALTER TABLE `declarationrevenus`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_dossier` (`id_dossier`);

--
-- Index pour la table `documentadministratif`
--
ALTER TABLE `documentadministratif`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `dossierfiscale`
--
ALTER TABLE `dossierfiscale`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_user` (`id_user`);

--
-- Index pour la table `incident`
--
ALTER TABLE `incident`
  ADD PRIMARY KEY (`id`),
  ADD KEY `service_affecte` (`service_affecte`);

--
-- Index pour la table `serviceintervention`
--
ALTER TABLE `serviceintervention`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `utilisateur`
--
ALTER TABLE `utilisateur`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `assistantdocumentaire`
--
ALTER TABLE `assistantdocumentaire`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT pour la table `declarationrevenus`
--
ALTER TABLE `declarationrevenus`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT pour la table `documentadministratif`
--
ALTER TABLE `documentadministratif`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT pour la table `dossierfiscale`
--
ALTER TABLE `dossierfiscale`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT pour la table `incident`
--
ALTER TABLE `incident`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=47;

--
-- AUTO_INCREMENT pour la table `serviceintervention`
--
ALTER TABLE `serviceintervention`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT pour la table `utilisateur`
--
ALTER TABLE `utilisateur`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `assistantdocumentaire`
--
ALTER TABLE `assistantdocumentaire`
  ADD CONSTRAINT `assistantdocumentaire_ibfk_1` FOREIGN KEY (`id_utilisateur`) REFERENCES `utilisateur` (`id`),
  ADD CONSTRAINT `assistantdocumentaire_ibfk_2` FOREIGN KEY (`id_document`) REFERENCES `documentadministratif` (`id`);

--
-- Contraintes pour la table `declarationrevenus`
--
ALTER TABLE `declarationrevenus`
  ADD CONSTRAINT `declarationrevenus_ibfk_1` FOREIGN KEY (`id_dossier`) REFERENCES `dossierfiscale` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `dossierfiscale`
--
ALTER TABLE `dossierfiscale`
  ADD CONSTRAINT `dossierfiscale_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `utilisateur` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `incident`
--
ALTER TABLE `incident`
  ADD CONSTRAINT `incident_ibfk_1` FOREIGN KEY (`service_affecte`) REFERENCES `serviceintervention` (`id`) ON DELETE SET NULL;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
