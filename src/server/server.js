'use strict';

require('dotenv').config();
require('make-promises-safe');
const fastify = require('fastify')({
    logging: process.env.LOGGING_ENABLED
});
const {Sequelize, DataTypes} = require('sequelize');
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

fastify.get('/', async (request, reply) => {
    if (process.env.ACTIVE === true) {
        return {"code": 0};
    } else {
        return {"code": 1};
    }
});


fastify.post('/', opts, async (request, reply) => {
    if (!process.env.DB_ENABLED) {
        return {code: 1, reason: 'disabled'};
    }

    const reqBody = request.body;
    const appKey = reqBody.key;
    if (appKey !== process.env.APP_KEY) {
        return {code: 1, reason: 'Failed'};
    }

    const type = reqBody.type;

    try {
        if (type === 'category') {
            const exists = await AppCategory.findOne({
                where: {
                    app_name: reqBody.app_name
                }
            });
            if (!exists) {
                await AppCategory.create({
                    app_name: reqBody.app_name,
                    category: reqBody.category,
                    app_package: reqBody.app_package
                });
            } else {
                // if it already exists, for now do nothing
            }

        } else if (type === 'session') {
            const exists = await SessionData.findOne({
                where: {
                    uid: reqBody.uid
                }
            });
            if (!exists) {
                await SessionData.create({
                    uid: reqBody.uid,
                    session_data: reqBody.session_data
                });
            } else {
                //    same as above
            }

        } else if (type === 'user') {
            const exists = await User.findOne({
                where: {
                    uid: reqBody.uid
                }
            });
            if (!exists) {
                await User.create({
                    uid: reqBody.uid,
                    sias: reqBody.sias
                });
            } else {
                //    same as above
            }
        } else {
            return {code: 1, reason: 'failed'};
        }
    } catch (e) {
        console.log(e);
        fastify.log.error(e);
        return {code: 1, reason: 'dbfail'};
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

