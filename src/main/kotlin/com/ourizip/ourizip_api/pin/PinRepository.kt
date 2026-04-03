package com.ourizip.ourizip_api.pin

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PinRepository : JpaRepository<Pin, Long>
