'use strict';

require('dotenv').config();
require('make-promises-safe');
const fastify = require('fastify')({
    logging: process.env.LOGGING_ENABLED
});
const { Sequelize, DataTypes } = require('sequelize');
const opts = {
    bodyLimit: 5242880 // increase the body limit size in case the data is too big. 5mb should be enough, but will test
};

// set up the database connection
const sequelize = new Sequelize(process.env.DB_DATABASE, process.env.DB_USER, process.env.DB_PASSWORD, {
    host: process.env.DB_HOST,
    port: process.env.DB_PORT,
    dialect: 'mariadb',
    dialectOptions: {
        timezone: process.env.DB_TIMEZONE
    },
    define: {
        timestamps: false
    },
});

// declare the models in the database
const User = sequelize.define('User', {
    uid: {
        type: DataTypes.STRING(1024),
        allowNull: false,
        primaryKey: true,
    },
    sias: {
        type: DataTypes.INTEGER
    }
}, {
    tableName: 'user',
    timestamps: false
});

const SessionData = sequelize.define('SessionData', {
    id: {
        type: DataTypes.INTEGER,
        primaryKey: true,
        autoIncrement: true
    },
    uid: {
        type: DataTypes.STRING(1024),
        allowNull: false
    },
    session_data: {
        type: DataTypes.TEXT('long')
    }
}, {
    tableName: 'user_session_data',
    timestamps: false
});

const AppCategory = sequelize.define('AppCategory', {
    app_name: {
        type: DataTypes.STRING(1024),
        allowNull: false,
        primaryKey: true
    },
    category: {
        type: DataTypes.STRING(1024)
    },
    app_package: {
        type: DataTypes.STRING(1024)
    }
}, {
    tableName: 'app_categories',
    timestamps: false
});

sequelize.sync();
fastify.post('/', opts, async (request, reply) => {
    if (!process.env.DB_ENABLED) {
        return {code: 1, reason: 'disabled'};
    }

    const reqBody = request.body;
    // todo: look at how to implement a key or something that gets sent from the phone
    const type = reqBody.type;
    let result;

    if (type === 'category') {
        result = await AppCategory.create({
            app_name: reqBody.app_name,
            category: reqBody.category,
            app_package: reqBody.app_package
        });
    } else if (type === 'session') {
        result = await SessionData.create({
            uid: reqBody.uid,
            session_data: reqBody.session_data
        });
    } else if (type === 'user') {
        result = await User.create({
            uid: reqBody.uid,
            sias: reqBody.sias
        });
    } else {
        return {code: 1, reason: 'failed'};
    }

    return {code: 0, reason: 'success'};
});

// Start listening.
fastify.listen(process.env.PORT || 3000, (err) => {
    if (err) {
        fastify.log.error(err);
        process.exit(1);
    }
});

