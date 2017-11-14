package com.heatledger.embed.sample;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.heatledger.Transaction;
import com.heatledger.embed.replicator.BasicReplicator;
import com.heatledger.replicate.DBUtils;
import com.heatledger.replicate.Replicator;
import com.heatledger.replicate.ResultSetIterator;
import com.heatledger.replicate.Replicator.Engine;

public class SampleMessageDB extends BasicReplicator<SampleMessage> {
    
    String CLEAR;
    String INSERT;
    String SELECT;

    public SampleMessageDB() {
        super("sample_message");
        if (Replicator.getEngine() == Engine.H2) {
            CLEAR = sql.get("CLEAR_SAMPLE_MESSAGES");
            INSERT = sql.get("INSERT_SAMPLE_MESSAGE");
            SELECT = sql.get("SELECT_SAMPLE_MESSAGE");
        }
    }

    @Override
    public void clear() {
        batchUpdate(new String[]{CLEAR});
    }

    @Override
    protected SampleMessage load(ResultSet rs) throws SQLException {
        return new SampleMessage(
                rs.getLong("id"),
                rs.getInt("timestamp"),
                rs.getLong("sender"),
                rs.getLong("recipient"),
                rs.getString("contents")
        );
    }
    
    public void create(Transaction transaction, SampleMessageMessage message) throws SQLException {
        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement(INSERT)) {
            pstmt.setLong(1, transaction.getId());
            pstmt.setLong(2, transaction.getTimestamp());
            pstmt.setLong(3, transaction.getSenderId());
            pstmt.setLong(4, transaction.getRecipientId());
            pstmt.setString(5, message.contents);
            pstmt.executeUpdate();
        }
    }    

    public ResultSetIterator<SampleMessage> search(String query, int from, int to) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(
                    SELECT 
                    + "WHERE contents LIKE ? "
                    + "ORDER BY timestamp ASC "
                    + DBUtils.limitsClause(from, to)
            );
            pstmt.setString(1, "'%" + query + "%'");
            DBUtils.setLimits(2, pstmt, from, to);
            return new ResultSetIterator<>(con, pstmt, this::load);
        } catch (SQLException e) {
            DBUtils.close(con, pstmt);
            Replicator.getInstance().handleSQLException(e);
            return null;
        }
    }    
    
    public ResultSetIterator<SampleMessage> list(int from, int to) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(
                    SELECT 
                    + "ORDER BY timestamp ASC "
                    + DBUtils.limitsClause(from, to)
            );
            DBUtils.setLimits(1, pstmt, from, to);
            return new ResultSetIterator<>(con, pstmt, this::load);
        } catch (SQLException e) {
            DBUtils.close(con, pstmt);
            Replicator.getInstance().handleSQLException(e);
            return null;
        }
    }     
}
