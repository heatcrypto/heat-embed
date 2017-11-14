/*
 * Copyright (C) Heat Ledger Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Written by:
 *   - DM de Klerk <dennis@heatledger.com>, July 2017
 * */
package com.heatledger.embed.replicator;

import com.heatledger.replicate.AbstractReplicator;
import com.heatledger.replicate.Replicator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class BasicReplicator<T> extends AbstractReplicator {

    public BasicReplicator(String id) {
        super(id);
    }

    @Override
    public void registerListeners() {}

    @Override
    public void beforeFullRescan() {}

    @Override
    public void afterFullRescan() {}

    public abstract void clear();

    protected abstract T load(ResultSet rs) throws SQLException;

    protected T find(String sql, long id) {
        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return load(rs);
            }
        } catch (SQLException e) {
            Replicator.getInstance().handleSQLException(e);
        }
        return null;
    }
    
    protected T find(String sql, String key) {
        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, key);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return load(rs);
            }
        } catch (SQLException e) {
            Replicator.getInstance().handleSQLException(e);
        }
        return null;
    }    

}
