package br.edu.ifpi.ifala.notificacao;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório para acessar e manipular entidades Notificacao no banco de dados.
 */
public interface NotificationRepository extends JpaRepository<Notificacao, Long> { }
