package br.edu.ifpi.ifala.notificacao;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório de Notificacao.
 */
public interface NotificationRepository extends JpaRepository<Notificacao, Long> {}
